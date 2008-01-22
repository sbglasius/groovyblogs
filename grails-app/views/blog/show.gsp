  
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
          <meta name="layout" content="main" />
         <title>Show Blog</title>
    </head>
    <body>
        <div class="body">
           <h1>Recent Entries</h1>
           <g:if test="${flash.message}">
                 <div class="message">${flash.message}</div>
           </g:if>
           <div style="margin: 1em; padding: 1em; border: 1px solid;">
           <div class="dialog">
                 <table>
                   
                   <tbody>
                   
                        <tr class="prop">
                              <td valign="top" class="name">Title:</td>
                              
                                    <td valign="top" class="value">${blog.title}</td>
                              
                        </tr>
                   
                        <tr class="prop">
                              <td valign="top" class="name">Description:</td>
                              
                                    <td valign="top" class="value">${blog.description}</td>
                              
                        </tr>
                   
                   
                        <tr class="prop">
                              <td valign="top" class="name">Feed Url:</td>
                              
                                    <td valign="top" class="value"><a href="${blog.feedUrl}">${blog.feedUrl}</a></td>
                              
                        </tr>
                        
                        <tr class="prop">
                              <td valign="top" class="name">Registered:</td>
                              
                                    <td valign="top" class="value"><g:niceDate date="${blog.registered}"/></td>
                              
                        </tr>                        
                   
                        <tr class="prop">
                              <td valign="top" class="name">Last Polled:</td>
                              
                                    <td valign="top" class="value"><g:dateFromNow date="${blog.lastPolled}"/></td>
                              
                        </tr>
                   
                        <tr class="prop">
                              <td valign="top" class="name">Next Poll:</td>
                              
                                    <td valign="top" class="value"><g:dateFromNow date="${blog.nextPoll}"/></td>
                              
                        </tr>
                   
                        <tr class="prop">
                              <td valign="top" class="name">Owner:</td>
                              
                                    <td valign="top" class="value">${blog?.account?.userid}</td>
                              
                        </tr>
                   
                   
                   </tbody>
                 </table>
           </div>
           </div>
           
           <div id="myBlogs">
	           <g:each var="entry" in="${sortedEntries}">
	           	
	           	
	           	 <div class="entry">
           			<div class="entryTitle">
	           			<g:link controller="entries" action="jump" id="${entry.id}">
           					${entry.title}
           				</g:link>
           				<g:translate entry="${entry}"/>
           			</div>
           			<div class="entryDetails">
           				${entry.blog.title} [ ${entry.hitCount} clicks ] -  
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
           			
           		</div>
	           	
	           </g:each>
	       </div>
           
        </div>
    </body>
</html>
