<html>
    <head>
        <title><g:layoutTitle default="Grails" /></title>
           <link rel="stylesheet" href="${createLinkTo(dir:contextPath,file:'plugins/iwebkit-0.2/css/style.css')}" type="text/css" />
        <link rel="shortcut icon" href="${createLinkTo(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
        <script type="text/javascript" src="${createLinkTo(dir:contextPath,file:'plugins/iwebkit-0.2/js/functions.js')}" ></script>
	 <meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<meta content="minimum-scale=1.0, width=device-width, maximum-scale=0.6667, user-scalable=no" name="viewport" />
        <ga:trackPageview/>

     </head>
    <body class="${pageProperty(name:'body.class')}">
    <g:layoutBody />		
    </body>	
</html>