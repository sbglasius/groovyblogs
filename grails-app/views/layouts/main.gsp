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
					
	</head>
	<body>
	<div id="doc3" class="yui-t4"> 
	    <div id="hd">
			<img id="logo" src="${createLinkTo(dir:'images',file:'headerlogo.png')}" alt="gZone Logo"/>
			
			  <div id="tabs">
			    <ul>
			      <!-- <li class="${request.requestURI =~ /home/ ? 'current' : 'notcurrent'}"><a href="<g:createLinkTo dir=''/>" >home</a></li> -->
			      <li id="${request.requestURI =~ /entries\/recent/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='recent'/>">Just In</a></li>
     			  <li id="${request.requestURI =~ /entries\/popular/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='popular'/>">Popular</a></li>
     			  <li id="${request.requestURI =~ /entries\/lists/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='entries' action='lists'/>">The Lists</a></li>
     			  <li id="${request.requestURI =~ /account/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='account' action='edit'/>">My Blogs</a></li>
     			  <g:if test="${session.account}">
			      	<li id="${request.requestURI =~ /login/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='login' action='logout'/>">Logout</a></li>
			      </g:if>
			      <g:else>
			      <li id="${request.requestURI =~ /login/ ? 'current' : 'notcurrent'}"><a href="<g:createLink controller='login' action='login'/>">Login</a></li>
			      </g:else>
			      <g:if test="${request.requestURI =~ /search/}">
				      <li id="current"><a href="<g:createLink controller='search' action='search'/>">Search</a></li>
			      </g:if>
			      <g:if test="${request.requestURI =~ /blog\/show/}">
				      <li id="current"><a href="<g:createLink controller='blog' action='list' id='params.id'/>">Blog Info</a></li>
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