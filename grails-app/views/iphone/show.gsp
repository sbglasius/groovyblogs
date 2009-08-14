<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="iphone" />
        <title>groovyblogs &raquo; ${entry.title}</title>
    </head>
    <body>
        <iphone:topbar title="${pageTitle}">
            <iphone:leftnavigation navtype="arrow">
                <iphone:navelement action="recent" content="Recent Entries"/>
            </iphone:leftnavigation>
        </iphone:topbar>
        <iphone:content>
            <iphone:section>

                <iphone:textbox header="${entry.title}">
                    <div style="color: gray; font-size: smaller;">
                        ${entry.blog.title} &raquo; ${entry.hitCount} clicks &raquo;
                        <g:dateFromNow date="${entry.dateAdded}"/>
                    </div>
                </iphone:textbox>

				<!--
                <g:link controller="entries" action="jump" id="${entry.id}">
                    <img alt="no thumb" src="${g.createLink(controller: 'thumbnail', action: 'show', id: entry.id)}" style="float: left;"/>
                </g:link>
				-->

                <g:summariseEntry description="${entry.description}"/><br/><br/>

                <g:link controller="entries" action="jump" id="${entry.id}">
                    Visit &raquo;
                </g:link>

            </iphone:section>

        </iphone:content>

    </body>
</html>
