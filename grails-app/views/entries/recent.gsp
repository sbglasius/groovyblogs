
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'thumbs.css')}"/>
         
         <title>${pageTitle}</title>
    </head>
    <body>
        <div class="body">
           <h1>${pageTitle}</h1>
           
           <g:each var="entry" in="${entries}">
           
           		<div class="entry">
           		<table>
           		<tr>
           		<g:if test="${thumbnails}">
           		<td style="padding-right: 60px">
           			<div id="pic">
						<a class="p1" href="<g:createLink controller='entries' action='jump' id='${entry.id}'/>" title="thumbnail image">
						<img src="<g:createLink controller='thumbnail' action='show' id='${entry.id}'/>" alt="No Image Available" />
						<img src="<g:createLink controller='thumbnail' action='showLarge' id='${entry.id}'/>" alt="No Image Available" class="large"  />
						</a>
					</div>
				</td>
				</g:if>
				<td valign="top" style="padding-top: 1em">
           			<div class="entryTitle">
	           			<g:link controller="entries" action="jump" id="${entry.id}">
           					${entry.title}
           				</g:link>
           				<g:translate entry="${entry}"/>
           			</div>
           			<div class="entryDetails">
					<g:link controller="blog" action="show" id="${entry.blog.id}" style="">${entry.blog.title}</g:link>
					 [ ${entry.hitCount} clicks ] -  
					<g:dateFromNow date="${entry.dateAdded}"/>
           			</div>
           			<div class="entrySummary">
           				<g:summariseEntry description="${entry.description}"/>
           			</div>
           			<g:if test="${entry.info}">
           			
           				<div class="entryInfo">
           					${entry.info}
           				</div>
           			
           			</g:if>
           		</td>
           		</tr></table>
           			
           		</div>
           
           </g:each>
            
        </div>
    </body>
</html>
            