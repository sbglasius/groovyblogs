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
     <script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-1038671-2");
pageTracker._trackPageview();
} catch(err) {}</script>
     </head>
    <body class="${pageProperty(name:'body.class')}">
    <g:layoutBody />		
    </body>	
</html>