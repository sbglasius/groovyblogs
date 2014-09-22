<%@ page import="org.groovyblogs.Blog" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blog.label', default: 'Blog')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div id="list-blog" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>

    <div class="table-responsive table-blogs">
        <table class="table table-striped">
            <thead>
            <tr>
                <g:sortableColumn property="title" title="${message(code: 'blog.title.label', default: 'Title')}"/>

                <g:sortableColumn property="description" title="${message(code: 'blog.description.label', default: 'Description')}"/>

                <sec:ifAllGranted roles="ROLE_ADMIN">
                    <g:sortableColumn property="feedUrl" title="${message(code: 'blog.feedUrl.label', default: 'Feed Url')}"/>

                    <g:sortableColumn property="pollFrequency" title="${message(code: 'blog.pollFrequency.label', default: 'Poll Frequency')}"/>

                    <g:sortableColumn property="status" title="${message(code: 'blog.status.label', default: 'Status')}"/>

                    <g:sortableColumn property="lastError" title="${message(code: 'blog.lastError.label', default: 'Last Error')}"/>
                </sec:ifAllGranted>

            </tr>
            </thead>
            <tbody>
            <g:each in="${blogInstanceList}" status="i" var="blogInstance">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                    <td><g:link action="show" id="${blogInstance.id}">${fieldValue(bean: blogInstance, field: "title")}</g:link></td>

                    <td>${fieldValue(bean: blogInstance, field: "description")}</td>

                    <sec:ifAllGranted roles="ROLE_ADMIN">
                        <td style="max-width: 300px;word-wrap: break-word"><a href="${blogInstance.feedUrl}" target="_blank">${fieldValue(bean: blogInstance, field: "feedUrl")}</a></td>

                        <td>${fieldValue(bean: blogInstance, field: "pollFrequency")}</td>

                        <td>${fieldValue(bean: blogInstance, field: "status")}</td>

                        <td style="max-width: 300px;word-wrap: break-word">${fieldValue(bean: blogInstance, field: "lastError")}</td>
                    </sec:ifAllGranted>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <div>
        <g:form action="checkPendingBlogs" method="POST">
            <g:each in="${blogInstanceList}" var="blog" status="i">
                <g:hiddenField name="blog.id" value="${blog.id}"/>
            </g:each>
            <g:hiddenField name="max" value="${params.max}"/>
            <g:hiddenField name="offset" value="${params.offset}"/>
            <g:hiddenField name="sort" value="${params.sort}"/>
            <g:hiddenField name="order" value="${params.order}"/>
            <button class="btn btn-primary">Check pending blogs</button>
        </g:form>
    </div>

    <div class="pagination">
        <g:paginate total="${blogInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
