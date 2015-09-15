<%@ page defaultCodec="html" %>
<%@ page import="org.springframework.util.ClassUtils" %>
<%@ page import="grails.plugin.searchable.internal.lucene.LuceneUtils" %>
<%@ page import="grails.plugin.searchable.internal.util.StringQueryUtils" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title><g:if test="${params.q && params.q?.trim() != ''}">${params.q} -</g:if> Search Result</title>
</head>

<body>
<g:set var="haveQuery" value="${params.q?.trim()}"/>
<g:set var="haveResults" value="${searchResult?.results}"/>
<div class="title">
    <span>
        <g:if test="${haveQuery && haveResults}">
            Showing <strong>${searchResult.offset + 1}</strong> - <strong>${searchResult.results.size() + searchResult.offset}</strong> of <strong>${searchResult.total}</strong>
            results for <strong>${params.q}</strong>
        </g:if>
        <g:else>
            &nbsp;
        </g:else>
    </span>
</div>

<g:if test="${haveQuery && !haveResults && !parseException}">
    <div class="alert alert-info">

        <p>Nothing matched your query - <strong>${params.q}</strong></p>
    </div>
</g:if>

<g:if test="${searchResult?.suggestedQuery}">
    <p>Did you mean <g:link controller="searchable" action="index" params="[q: searchResult.suggestedQuery]">${StringQueryUtils.highlightTermDiffs(params.q.trim(), searchResult.suggestedQuery)}</g:link>?</p>
</g:if>

<g:if test="${parseException}">
    <div class="alert alert-warning">
        <p>Your query - <strong>${params.q}</strong> - does not yield a is not valid.</p>

        <p>Suggestions:</p>
        <ul>
            <li>Fix the query: see <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for examples</li>
            <g:if test="${LuceneUtils.queryHasSpecialCharacters(params.q)}">
                <li>Remove special characters like <strong>" - [ ]</strong>, before searching, eg, <em><strong>${LuceneUtils.cleanQuery(params.q)}</strong></em><br/>
                    <em>Use the Searchable Plugin's <strong>LuceneUtils#cleanQuery</strong> helper method for this: <g:link controller="searchable" action="index"
                                                                                                                            params="[q: LuceneUtils.cleanQuery(params.q)]">Search again with special characters removed</g:link>
                    </em>
                </li>
                <li>Escape special characters like <strong>" - [ ]</strong> with <strong>\</strong>, eg, <em><strong>${LuceneUtils.escapeQuery(params.q)}</strong></em><br/>
                    <em>Use the Searchable Plugin's <strong>LuceneUtils#escapeQuery</strong> helper method for this: <g:link controller="searchable" action="index"
                                                                                                                             params="[q: LuceneUtils.escapeQuery(params.q)]">Search again with special characters escaped</g:link>
                    </em><br/>
                    <em>Or use the Searchable Plugin's <strong>escape</strong> option: <g:link controller="searchable" action="index"
                                                                                               params="[q: params.q, escape: true]">Search again with the <strong>escape</strong> option enabled</g:link></em>
                </li>
            </g:if>
        </ul>
    </div>
</g:if>

<g:if test="${haveResults}">
    <div class="results">
        <g:each var="result" in="${searchResult.results}" status="index">
            <g:if test="${result.getClass() == org.groovyblogs.BlogEntry}">
                <tmpl:/entries/entry entry="${result}"/>
            </g:if>
        </g:each>
    </div>

    <div class="text-center">
        <g:set var="totalPages" value="${Math.ceil(searchResult.total / searchResult.max)}"/>
        <g:if test="${totalPages == 1}">
            <ul class="pagination">
                <li class="prev disabled"><span><i class="fa fa-chevron-left"></i></span></li>
                <li class="active"><span>1</span></li>
                <li class="next disabled"><span><i class="fa fa-chevron-right"></i></span></li>
            </ul>

        </g:if>
        <g:else><g:paginate controller="searchable" action="index" params="[q: params.q]" total="${searchResult.total}"/></g:else>
    </div>
</g:if>
<asset:script>
    $('*[data-thumbnail]').fetchThumbnail();
</asset:script>

</body>
</html>
