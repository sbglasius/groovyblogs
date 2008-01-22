
import org.apache.commons.logging.LogFactory
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.DateTools
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.MultiFieldQueryParser
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.HitIterator;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.analysis.TokenStream;



/**
* A Simple Grails Search Service which uses Lucene to do the heavy lifting.
* 
* @author Glen Smith (glen.smith@gmail.com)
*/

class SearchService  {
	
	//def log = LogFactory.getLog(this.class.name)
	
	// name relative to app root
	def indexDirectoryName = "groovyblogs-index"
	boolean transactional = false
	
	Directory dir
	
	static def updateLock = new Object()
	
	// Clobers the index directory
	def deleteIndex() {
		dir = FSDirectory.getDirectory(indexDirectoryName, true)
	}
	
	// Gets a handle to the lucene search directory object
	def getDirectory() {
		if (dir == null) {
			dir = FSDirectory.getDirectory(indexDirectoryName, isIndexEmpty())
		}
		return dir
	}
	
	// Search given object types fields
	def search(queryTerms, fields, int maxHits = 10, int offset = 0) {
		
		def startTime = System.currentTimeMillis()
		
		IndexSearcher is = new IndexSearcher(getDirectory());
		
		def totalDocsInIndex = is.maxDoc()
		log.debug("Total documents in index: [$totalDocsInIndex]")
				
		// Create a flag for each search field
		def flags = []
		fields.each { field -> 
			flags << BooleanClause.Occur.SHOULD
		}
		
		String[] fieldsArg = fields
		BooleanClause.Occur[] flagsArg = flags.toArray(new BooleanClause.Occur[0])
		
		Query query = MultiFieldQueryParser.parse(queryTerms, fieldsArg, flagsArg, new StandardAnalyzer());
		//Query query = new QueryParser(field, new StandardAnalyzer()).parse(queryTerms);
		def hitCount = 0
		OffsetCollector hc = new OffsetCollector(maxHits, offset)
		
		is.search(query, hc);
		
		def docs = []
		docs = hc.hits()
		def hits = []
		docs.each { docid ->
			log.debug("Getting document $docid")
			def doc = is.doc(docid)
			hits << doc 
		}
		
		// for keywork matching, we throw away wildcards in search term
		def niceQueryTerms = queryTerms.replaceAll("[*+)('\"]", "")
		Query niceQuery = MultiFieldQueryParser.parse(niceQueryTerms, fieldsArg, flagsArg, new StandardAnalyzer());
		
		def scorer = new QueryScorer(niceQuery)
		def formatter = new SimpleHTMLFormatter("<span class='highlight'>", "</span>")
		def highlighter = new Highlighter(formatter, scorer)
		def fragmenter = new SimpleFragmenter(50)
		highlighter.setTextFragmenter(fragmenter)

		
		def cacheHits = []
		
		for (h in hits) {
			// cache and return to prevent need to keep session open
			def highlightedFields = [:]			
			for (f in fields) {
				def textField = h.get(f)
				TokenStream stream = new StandardAnalyzer().tokenStream(f, new StringReader(textField))
				def bestFragments = highlighter.getBestFragments(stream, textField, 5)?.join("...")
				if (bestFragments == null || bestFragments.size() == 0) {
					// if no highlighted terms, take first 50 characters
					bestFragments = textField.substring(0, textField.size() < 50 ?  textField.size() : 50) + "..." // [0..49] + "..."
				}
				log.debug("[$f] had highlighted fragments of [$bestFragments]")
				highlightedFields.put(f,bestFragments)
			}
			def nextHit = new SearchResult(document: h, highlight: highlightedFields )
			cacheHits << nextHit
		}
		
		is.close()
		
		def endTime = System.currentTimeMillis()
		
		def searchResults = new SearchResults(resultList: cacheHits, totalHitCount: hc.hitCount(), 
								maxHitsRequested: hc.maxHits, returnedHitCount: cacheHits.size(),
								totalHitsOffset: offset, totalDocsInIndex: totalDocsInIndex,
								fields: fields, queryTerms: queryTerms,	queryTime: (endTime - startTime))
		
		return searchResults
		
	}
	
	// Synthetic key for storing in index
	def getObjId(obj) {
		
		return "(" + obj.id + "-" + obj.class.name + ")"
		
	}
	
	// Creates a new Lucene Document object for indexing
	def getDocument(obj) {
		
		def fieldMap = obj.indexedFields()
		Document doc = new Document();
		fieldMap.each { key, value ->
			// Need to convert dates to strings for indexing
			if (value instanceof Date) {
				value = DateTools.dateToString(value, DateTools.Resolution.SECOND)
			}
			doc.add(new Field(key, value, Field.Store.YES, Field.Index.TOKENIZED));
		}
		doc.add(new Field("id", "$obj.id", Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("class", obj.class.name, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("id-class", getObjId(obj), Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		return doc
	}
	
	// Takes a list of objects for indexing
	def indexAll(objs) {

		synchronized (updateLock) {

			def createIndex = isIndexEmpty()
			IndexWriter iw = new IndexWriter(getDirectory(), new StandardAnalyzer(), createIndex);
			
			objs.each { nextObj ->
				def doc = getDocument(nextObj)
				iw.addDocument(doc);
			}		
			iw.close();
		}
		
	}
	
	// Takes a single object for indexing
	def index(obj) {

		indexAll( [ obj ])
		
	}

	// Optimises the index for searching
	def optimise() {

		synchronized (updateLock) {
			def createIndex = isIndexEmpty()
			IndexWriter iw = new IndexWriter(getDirectory(), new StandardAnalyzer(), createIndex);
			iw.optimize(); 
			iw.close()
		}
		
	}

	
	// Removes an object from the index
	def unindex(obj) {

		unindex([ obj ])
		
	}
	
	// Removes a list of objects from the index
	def unindexAll(objs) {

		synchronized (updateLock) {
			objs.each { obj ->
				IndexReader ir = IndexReader.open(getDirectory());
				ir.deleteDocuments(new Term("id-class", getObjId(obj)));
				ir.close();
			}
		}
	}
	
	// Updates a single object in the index
	def reindex(obj) {
		reindexAll([ obj ])
	}
	
	// Updates a list of objects in the index
	def reindexAll(objs) {
		unindexAll(objs)
		indexAll(objs)
	}
	
	
	def isIndexEmpty() {
		
		File f = new File(indexDirectoryName)
		if (!f.exists())
			return true
			
		// segment file means we're searchable... hmm... must be better way	
		def files = Arrays.asList(f.list())
		def indexEmpty = !(files.contains("segments"))
		return indexEmpty
		
	}
	
	
	
}

