<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><g:layoutTitle default="Groovy Blogs"/></title>
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
%{--
                    <li class="active"><a href="#">Home</a></li>
                    <li><a href="#about">About</a></li>
                    <li><a href="#contact">Contact</a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="#">Action</a></li>
                            <li><a href="#">Another action</a></li>
                            <li><a href="#">Something else here</a></li>
                            <li class="divider"></li>
                            <li class="dropdown-header">Nav header</li>
                            <li><a href="#">Separated link</a></li>
                            <li><a href="#">One more separated link</a></li>
                        </ul>
                    </li>
--}%
                </ul>
                <g:form url='[controller: "searchable", action: "index"]' id="searchableForm" name="searchableForm" method="get" class="navbar-form navbar-right">
                    <g:textField name="q" value="${params.q}" size="25" class="form-control" placeholder="Search the blogs..."/>
                </g:form>

                <ul class="nav navbar-nav navbar-right">
%{--
                    <li><a href="../navbar/">Default</a></li>
                    <li><a href="../navbar-static-top/">Static top</a></li>
                    <li class="active"><a href="./">Fixed top</a></li>
--}%
                </ul>
            </div><!--/.nav-collapse -->
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">

            <g:layoutBody/>
        </div>
    </div>
</div>
<asset:javascript src="application.js"/>
<ga:trackPageview />
</body>
</html>