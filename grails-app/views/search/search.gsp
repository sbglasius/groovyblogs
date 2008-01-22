
  
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <title>Search Results</title>
    </head>
    <body>
        <div class="body">
            <g:if test="${flash.message}">
                 <div class="message">
                       ${flash.message}
                 </div>
            </g:if>
        </div>

		<g:if test="${results != null}">
		
			<div id="searchCount">
				<g:if test="${results.totalHitCount > 0}">
					Displaying <b>${1 + results.totalHitsOffset} - ${results.totalHitsOffset + results.returnedHitCount}</b> 
					of <b>${results.totalHitCount}</b> matches 
				</g:if>
				<g:else>
					No matches found. 
				</g:else>	
				(<b>${results.queryTime}</b> ms).  Index contains <b>${results.totalDocsInIndex}</b> documents.
			</div>
			
			<div id="searchBody">
				<g:searchResults results="${results}" titleField="title" bodyField="description"/>
			</div>
			
			<div id="searchCrumbs">
				<g:searchCrumbs results="${results}"/>
			</div>
		</g:if>
    </body>
</html>
            