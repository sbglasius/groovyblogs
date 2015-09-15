<g:if test="${account.blogs.size() > 0}">

    <g:hasErrors bean="${blog}">
        <div class="errors">
            <g:renderErrors bean="${blog}" as="list"/>
        </div>
    </g:hasErrors>

    <table class="table table-bordered table-striped">
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

</g:if>
<g:else>
    <p>You do not have any blogs</p>
</g:else>