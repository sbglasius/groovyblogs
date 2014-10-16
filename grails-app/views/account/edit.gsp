
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Account</title>
        <g:javascript library="scriptaculous" />

    </head>
    <body>

        <div class="body">
            <h1>Edit Account</h1>

            <div class="pad-bottom-10"><g:hasErrors bean="${account}">
                <div class="errors">
                    <g:renderErrors bean="${account}" as="list"/>
                </div>
            </g:hasErrors>
            <g:form controller="account" action="update" method="post">
                <f:with bean="account">
                    <input type="hidden" name="id" value="${account?.id}"/>
                    <f:display property="username"/>
                    <f:field property="password">
                        <g:passwordField name="${property}" class="form-control"/>
                    </f:field>
                    <f:field property="email"/>
                </f:with>
                <div class="buttons">
                    <g:actionSubmit class="btn btn-primary" value="Update"/>
                </div>

            </g:form></div>


            <g:form action="addFeed" method="post" >


                <g:if test="${account.blogs.size() > 0}">
                    <h2>My Blogs</h2>

                    <div id="myBlogs">

                        <g:hasErrors bean="${blog}">
                            <div class="errors">
                                <g:renderErrors bean="${blog}" as="list" />
                            </div>
                        </g:hasErrors>

                        <table class="blogTable">
                            <tr>
                                <td>Blog</td>
                                <td>Description</td>
                                <td>Added</td>
                                <td>Last Update</td>
                                <td>Next Update</td>
                                <td></td>
                            </tr>


                            <g:each var="blog" in="${account.blogs}">


                                <tr class="myBlog" id="myBlog-${blog.id}">
                                    <td><a href="${blog.feedUrl}">${blog.title}</a></td>
                                    <td>${blog.description}</td>
                                    <td><g:niceDate date="${blog.registered}"/></td>

                                    <td id="lastPolled-${blog.id}"><g:dateFromNow date="${blog.lastPolled}"/></td>
                                    <td id="nextPolled-${blog.id}"><g:dateFromNow date="${blog.nextPoll}"/></td>

                                    <td>

                                        <a href="<g:createLink action='updateFeed' id='${blog.id}'/>">Update</a>
                                        <a href="<g:createLink action='deleteFeed' id='${blog.id}'/>" onclick="return confirm('Are you sure you want to delete ${blog.title}')">Delete</a>
                                    </td>
                                </tr>


                            </g:each>
                        </table>

                    </div>
                </g:if>
                <div id="newFeed" class="well well-lg">

                    <p style="margin-bottom: 5px; ">
                        groovyblogs.org hosts <a href="http://groovy.codehaus.org">Groovy</a> and
                        <a href="http://www.grails.org">Grails</a>-related blogs. If your feed is
                        not Groovy or Grails related, it will be filtered by the engine and will
                        not display. Use the <b>Test Feed</b> button to see what will and won't get aggregated.
                        All feed creation is moderated, so your blog won't appear right away.
                    </p>

                    <label for='feedUrl'>Add New Feed:</label><input type="text" name='feedUrl' style="width: 400px;" value="${blog?.feedUrl?.encodeAsHTML()}">
                    <g:submitToRemote url="[action: 'testFeed', controller: 'account']" update="feedResults"
                               onLoading="showSpinner();" onLoaded="hideSpinner();"
                               onComplete="appearDiv();" value="Test Feed"/>

                    <img id="spinner" style="display: none;" src="${createLinkTo(dir:'images',file:'spinner.gif')}">

                    <input type="submit" value="Add Feed"/>



                </div>

            </g:form>

            <div id="feedResults">
                <!-- placeholder for Ajax content -->
            </div>

            <script language="javascript">

                function showSpinner() {
                    document.all.spinner.style.display = "inline";
                    // new Effect.Fade('feedResults');
                }

                function hideSpinner() {
                    document.all.spinner.style.display = "none"
                }

                function appearDiv() {
                    new Effect.Appear('feedResults');
                }


            </script>


        </div>
    </body>
</html>
