<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="iphone" />
        <title>groovyblogs &raquo; Recent Entries</title>
        <style>
            li.menu {
                padding-bottom: 1em;
            }
        </style>
    </head>
    <body class="list">
        <iphone:topbar title="groovyblogs">
            <iphone:leftnavigation navtype="button">
                <iphone:navelement action="pc" content="PC Website"/>
            </iphone:leftnavigation>
        </iphone:topbar>
        <iphone:content>
            <iphone:section>
                <iphone:list action="show" descriptionField="title" list="${entries}"/>
            </iphone:section>
        </iphone:content>

    </body>
</html>
