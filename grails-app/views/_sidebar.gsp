<div class="niceBox">
  <div class="niceBoxHd">About</div>
  <div class="niceBoxBody">
    groovyblogs.org was developed by
    <a href="http://blogs.bytecode.com.au/glen">Glen Smith</a>.
    <!--
                    Graciously hosted by <a href="http://www.contegix.com/">Contegix</a>.
    -->
    This site is running groovyblogs version
    <g:meta name="app.version"/> on
    Grails <g:meta name="app.grails.version"/>

  </div>

</div>


<div class="niceBox">
  <div class="niceBoxHd">Feeds</div>
  <div class="niceBoxBody">
    <ul>
      <li>
        <a href="<g:createLink controller='feed' action='atom'/>" class="feedLink">
          <img src="${createLinkTo(dir:'images',file:'feed-icon-16x16.jpg')}" alt="Atom"/>
          Atom </a>
      </li>
      <li>
        <a href="<g:createLink controller='feed' action='rss'/>" class="feedLink">
          <img src="${createLinkTo(dir:'images',file:'feed-icon-16x16.jpg')}" alt="RSS"/>
          RSS </a>
      </li>
	<li>
        <a href="http://twitter.com/groovyblogs" class="feedLink">
          <img src="${createLinkTo(dir:'images',file:'twitter-icon-16x16.png')}" alt="Twitter"/>
          Twitter </a>
      </li>
    </ul>
    <g:feedburner/>
  </div>
</div>


<jsec:user>

  <div class="niceBox">
    <div class="niceBoxHd">User Info</div>
    <div class="niceBoxBody">
      <p><a href="<g:createLink controller='account' action='edit'/>">
          <jsec:principal/>
        </a></p>
      <g:link controller="auth" action="signOut">Logout</g:link>

    </div>

  </div>


</jsec:user>
<jsec:notUser>
  <div class="niceBox">
    <div class="niceBoxHd">Login</div>
    <div class="niceBoxBody">


      <g:form controller="auth" action="signIn" method="post" >
        <b>User Id:</b>
        <input type='text' name='username'/>
        <b>Password:</b>
        <input type="password" name='password'/>
        <b>Remember Me:</b>
        <g:checkBox name="rememberMe" />

        <span class="formButton">
          <input type="submit" value="Login"></input>
        </span>
      </g:form>
      <p>
      <g:link controller='login' action="forgottenPassword">Forgotten your password?</g:link><p/>
      <g:link controller='account' action="signup">Need to Sign Up?</g:link>

    </div>

  </div>
</jsec:notUser>


<div class="niceBox">
  <div class="niceBoxHd">Preferences</div>
  <div class="niceBoxBody">
    <g:if test="${request.cookies.find { cookie -> cookie.name == 'lang' }}">
       English Only (<g:link controller="account" action="preferredLang" id="none">Show All Languages</g:link>)
    </g:if>
    <g:else>
      All Languages (<g:link controller="account" action="preferredLang" id="en">Show English Only</g:link>)
    </g:else>
  </div>
</div>



<div class="niceBox">
  <div class="niceBoxHd">Get the Source</div>
  <div class="niceBoxBody">
    Download the
    <a href="http://code.google.com/p/groovyblogs/">complete source code</a>
    to groovyblogs.org. Contribute patches and enhancements!
  </div>
</div>





<div class="niceBox">
  <div class="niceBoxHd">Stats</div>
  <div class="niceBoxBody">
    <g:recentStats/>
    <%-- <g:recentChart/> --%>
    
    <img style="margin-left: -5px; margin-top: 5px;" src="<g:createLink controller='chart' action='siteStats'/>" alt="site stats"/>
    
    </div>
</div>

<div class="niceBox">
  <div class="niceBoxHd">Newest Bloggers</div>
  <div class="niceBoxBody">
    <g:recentBloggers/>
  </div>
</div>


<div style="padding-left: 3em;">
  <p>
    <a href="http://www.grails.org/">
      <img src="${createLinkTo(dir: 'images', file: 'grails_button.gif')}" alt="Powered By Grails"/>
    </a>
  </p>
  <p> <!--
                        <a href="http://www.contegix.com/"><img src="${createLinkTo(dir: 'images', file: 'contegix_logo.jpg')}" width="145" height="41" border="0" alt="Hosted by Contegix"/></a>
    --> </p>

</div>

