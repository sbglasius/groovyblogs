<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="groovyblogs.org is a groovy and grails blog aggregator"/>
    <meta name="keywords" content="groovy,grails,blogs"/>
    <meta name="robots" content="index,follow"/>

    <title>groovyblogs.org - <g:layoutTitle default="Welcome"/></title>
    <link rel="shortcut icon" href="${assetPath(src: 'favicon.png')}" type="image/png">

    <asset:stylesheet src="application.css"/>
    <g:layoutHead/>
</head>

<body>
<div class="container">

    <div class="row">
        <div class="col-md-9 col-lg-10 col-sm-12 ">

            <g:layoutBody/>
        </div>
    </div>
</div>
<asset:javascript src="application.js"/>
<asset:deferredScripts/>
</body>
</html>