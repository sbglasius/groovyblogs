<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Mailing Lists</title>
</head>

<body>
<div class="body">
    <h1>Mailing Lists (Last 24 Hours)</h1>

    <p>Powered By <a href="http://www.nabble.com/">Nabble</a></p>

    <div class="row">
        <g:each var="entry" in="${entries}">

            <div class="col-md-12">
                <h4>
                    <a href="${entry.link}">
                        ${entry.title}
                        <small>
                            ${entry.info} - ${entry.author} -
                            <g:dateFromNow date="${entry.publishedDate}"/>
                        </small>
                    </a>
                </h4>
                <g:summariseEntry description="${entry.description}"/>
            </div>

        </g:each>
    </div>
</div>
</body>
</html>
