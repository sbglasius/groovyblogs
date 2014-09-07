<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${pageTitle}</title>

%{--
    // TODO: Make language selection work again
    <g:if test="${request.cookies.find { cookie -> cookie.name == 'lang' }}">
        <style>
        .nonenglish {
            visibility: hidden;
            height: 0px;
        }
        </style>
    </g:if>
--}%
</head>

<body>
<div class="body">
    <h1>${pageTitle}</h1>
    <g:each in="${entries}" var="entry">
        <g:render template="entry" model="[entry: entry, thumbnails: thumbnails]"/>
    </g:each>
    <asset:script>
        $('*[data-thumbnail]').fetchThumbnail();
    </asset:script>
</div>
</body>
</html>
