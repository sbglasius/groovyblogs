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
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js" type="text/javascript"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js" type="text/javascript"></script>
    <![endif]-->
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
<ga:trackPageview/>
<asset:deferredScripts/>
</body>
</html>