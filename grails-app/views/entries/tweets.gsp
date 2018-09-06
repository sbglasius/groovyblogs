

<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <title>Tweets</title>
         <style>
           /*
           .userScreenName {
             visibility: hidden;
             width: 0px;
           }
           */
         </style>
    </head>
    <body>
        <div class="body">
           <h1>Recent Tweets</h1>
           <p>Powered By <a href="http://www.groovytweets.org/">GroovyTweets</a></p>

           <g:each var="entry" in="${entries}">

           		<div class="entry">
           			<div class="entryTitle">
	           			${entry.description}
           			</div>
           			<div class="entryDetails">
                                        <a href="${entry.link}">Read</a> -  ${entry.author} -
           				<g:dateFromNow date="${entry.publishedDate}"/>
           			</div>
           			<div class="entrySummary">
           				
           			</div>
           		</div>

           </g:each>

        </div>
    </body>
</html>

            