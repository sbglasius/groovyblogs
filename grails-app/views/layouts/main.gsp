<html>
	<head>
		<title>groovyblogs.org - <g:layoutTitle default="Welcome" /></title>
		<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'reset-fonts-grids.css')}"/>
		<link rel="stylesheet" href="${createLinkTo(dir:'css',file:'groovyblogs.css')}"/>
		<link rel="shortcut icon" href="${createLinkTo(file:'favicon.ico')}" />
		<g:layoutHead />
		<g:javascript library="application" />

		
		<meta name="description" content="groovyblogs.org is a groovy and grails blog aggregator" />
		<meta name="keywords" content="groovy,grails,blogs" />
		<meta name="robots" content="index,follow" />

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
	<body>
	<div id="doc3" class="yui-t4"> 
	    <div id="hd">
			<img id="logo" src="${createLinkTo(dir:'images',file:'headerlogo.png')}" alt="gZone Logo"/>
			
			  <div id="tabs">
			    <ul>
			      <!-- <li class="${request.forwardURI =~ /home/ ? 'current' : 'notcurrent'}"><a href="<g:createLinkTo dir=''/>" >home</a></li> -->
			      <li id="${request.forwardURI =~ /entries\/recent/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='recent'/>">Just In</a></li>
     			  <li id="${request.forwardURI =~ /entries\/popular/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='popular'/>">Popular</a></li>
     			  <li id="${request.forwardURI =~ /entries\/lists/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='lists'/>">The Lists</a></li>
     			  <!-- <li id="${request.forwardURI =~ /entries\/tweets/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='tweets'/>">Tweets</a></li> -->
				<jsec:hasRole name="admin">
     			  <li id="${request.forwardURI =~ /blog\/list/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='blog' action='list'/>">All Blogs</a></li>
				</jsec:hasRole>

                          <li id="${request.forwardURI =~ /account/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='account' action='edit'/>">My Blogs</a></li>

                          <jsec:user>
			      	<li id="${request.forwardURI =~ /login/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='auth' action='signOut'/>">Logout</a></li>
			  </jsec:user>

                          <jsec:notUser>
			      <li id="${request.forwardURI =~ /login/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='auth' action='login'/>">Login</a></li>
			  </jsec:notUser>
			      <g:if test="${request.forwardURI =~ /search/}">
				      <li id="current"><a href="<g:createLink controller='search' action='search'/>">Search</a></li>
			      </g:if>

			      
			      
			    </ul>
			    <g:searchBox noCombo="true" query="${params.query}" fields="title,description" controller="search" action="search"/>
			  </div>
			
		</div>  
	   <div id="bd"> <!-- start body -->
	   
	  		<div id="yui-main"> 
	        	<div class="yui-b">
	        		<g:if test="${flash.message}">
	        			<div id="flash">
	        				${flash.message}
	        			</div>
	        		</g:if>
	        	
		        	<g:layoutBody />		
	        	</div> 
	      	</div> 
	      	<div class="yui-b">
             
	      		<g:render template="/sidebar"/>
	      	
	      	</div> 
	   


	   </div>  <!-- end body -->
	   <div id="ft">
	   		All article content copyright by respective authors. groovyblogs.org by <a href="http://blogs.bytecode.com.au/glen">Glen Smith</a>.
	   </div>  
	</div> 
		
	</body>	

</html>