<g:form action="addFeed" method="post">

    <p class="pad-bottom-10">
        groovyblogs.org hosts <a href="http://groovy.codehaus.org">Groovy</a> and
        <a href="http://www.grails.org">Grails</a>-related blogs. If your feed is
    not Groovy or Grails related, it will be filtered by the engine and will
    not display. Use the <b>Test Feed</b> button to see what will and won't get aggregated.
    All feed creation is moderated, so your blog won't appear right away.
    </p>

    <f:with bean="blog">
        <f:field property="feedUrl"/>
    </f:with>
    <g:submitToRemote url="[action: 'testFeed', controller: 'account']" update="feedResults" value="Test Feed"/>

    <img id="spinner" style="display: none;" src="${createLinkTo(dir: 'images', file: 'spinner.gif')}">

    <input type="submit" value="Add Feed"/>

    <div id="feedResults">
        <!-- placeholder for Ajax content -->
    </div>

</g:form>
