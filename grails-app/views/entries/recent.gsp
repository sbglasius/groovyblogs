
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <link rel="stylesheet" href="${createLinkTo(dir:'css',file:'thumbs.css')}"/>
        <%--
        <g:javascript library="prototype" />
        <g:javascript library="endless_page" />
        --%>
         
         <title>${pageTitle}</title>
    </head>
    <body>
        <div class="body">
           <h1>${pageTitle}</h1>
           
           <g:each var="entry" in="${entries}">
           
           		<g:render template="entry" model="[ entry: entry, thumbnails: thumbnails ]"/>
           
           </g:each>
            
        </div>
    </body>
</html>
            