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

    <div class="container-logo">
        <div class="row">
            <div class="col-xs-12">
                <h1 class="logo">
                    <g:link uri="/">
                        <asset:image src="groovy-logo.png" class="groovy-logo"/>
                        <asset:image src="blogs.png" class="groovy-blogs"/>
                    </g:link>
                </h1>
            </div>
        </div>
    </div>
    <!-- Fixed navbar -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target=".navbar-collapse">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            </div>

            <div class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">The lists<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><g:link controller='entries' action='recent'>Latest Blogs</g:link></li>
                            <li><g:link controller='entries' action='popular'>Popular Blogs</g:link></li>
                            <li><g:link controller='entries' action='lists'>Mailing Lists</g:link></li>
                        </ul>
                    </li>
                    <sec:ifAnyGranted roles="ROLE_ADMIN">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown">Admin<span class="caret"></span></a>
                            <ul class="dropdown-menu" role="menu">
                                <li><g:link controller='user' action='index'>Users</g:link></li>
                                <li><g:link controller='blog' action='index'>Blogs</g:link></li>
                                <li><g:link controller='blogEntry' action='index'>Blog-entries</g:link></li>
                                <li><g:link controller='quartz' action='index'>Quartz control</g:link></li>
                            </ul>
                        </li>
                    </sec:ifAnyGranted>
                </ul>
                <ul class="nav navbar-nav navbar-right visible-xs visible-sm">
                <sec:ifLoggedIn>
                    <li><g:form controller="logout">
                        <button class="btn btn-link">Logout</button>
                    </g:form></li>

                </sec:ifLoggedIn>
                <sec:ifNotLoggedIn>
                    <li><g:link controller="blog" action="index">Login</g:link></li>
                </sec:ifNotLoggedIn>
                </ul>

                <g:form url='[controller: "searchable", action: "index"]' id="searchableForm" name="searchableForm" method="get" class="navbar-form navbar-right">
                    <g:textField name="q" value="${params.q}" size="25" class="form-control" placeholder="Search the blogs..."/>
                </g:form>
                %{--
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="../navbar/">Default</a></li>
                    <li><a href="../navbar-static-top/">Static top</a></li>
                    <li class="active"><a href="./">Fixed top</a></li>
                </ul>
                --}%
            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="row">
        <div class="col-md-9 col-lg-10 col-sm-12 ">

            <g:layoutBody/>
        </div>
        <div class="col-md-3 col-lg-2 hidden-xs hidden-sm ">
            <tmpl:/layouts/sidebar/>
        </div>
    </div>
    <div class="row">
        <div class="visible-xs visible-sm col-sm-12">
            <tmpl:/layouts/sidebar/>
        </div>
    </div>
</div>
<asset:javascript src="application.js"/>
<ga:trackPageview/>
<asset:deferredScripts/>
</body>
</html>