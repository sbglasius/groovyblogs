
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <title>Mailing Lists</title>
    </head>
    <body>
        <div class="body">
           <h1>Mailing Lists (Last 24 Hours)</h1>
           <p>Powered By <a href="http://www.nabble.com/">Nabble</a></p>
           
           <g:each var="entry" in="${entries}">
           
           		<div class="entry">
           			<div class="entryTitle">
	           			<a href="${entry.link}">
           					${entry.title}
           				</a>
           			</div>
           			<div class="entryDetails">
           				${entry.info} - ${entry.author} -  
           				<g:dateFromNow date="${entry.publishedDate}"/>
           			</div>
           			<div class="entrySummary">
           				<g:summariseEntry description="${entry.description}"/>
           			</div>
           		</div>
           
           </g:each>
            
        </div>
    </body>
</html>
            