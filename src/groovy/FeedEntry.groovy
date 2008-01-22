

class FeedEntry {

    String title
    String description
    String summary  // short form for presentation (html stripped)
    String link
    String author
    String info // used for whatever you want to put there
    Date publishedDate
  
	public static String summarize(String longForm) {
		
		def summary = longForm ? longForm : ""
		summary = longForm.replaceAll("</?[^>]+>", "")
		summary = summary.length() > 200 ? summary[0..199] + "..." : summary 
				
    }
    
    public String getLanguage() {
    
    	def guesser = new org.knallgrau.utils.textcat.TextCategorizer()
    	def category = description ? guesser.categorize(description) : ""
    	return category

    }


}