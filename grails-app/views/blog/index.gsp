<%@ page import="org.groovyblogs.Blog" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blog.label', default: 'Blog')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-blog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-blog" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table class="table table-striped">
        <thead>
        <tr>
            <g:sortableColumn property="title" title="${message(code: 'blog.title.label', default: 'Title')}"/>

            <g:sortableColumn property="description" title="${message(code: 'blog.description.label', default: 'Description')}"/>

            <g:sortableColumn property="feedUrl" title="${message(code: 'blog.feedUrl.label', default: 'Feed Url')}"/>

            <g:sortableColumn property="pollFrequency" title="${message(code: 'blog.pollFrequency.label', default: 'Poll Frequency')}"/>

            <g:sortableColumn property="status" title="${message(code: 'blog.status.label', default: 'Status')}"/>

            <g:sortableColumn property="lastError" title="${message(code: 'blog.lastError.label', default: 'Last Error')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${blogInstanceList}" status="i" var="blogInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${blogInstance.id}">${fieldValue(bean: blogInstance, field: "title")}</g:link></td>

                <td>${fieldValue(bean: blogInstance, field: "description")}</td>

                <td><a href="${blogInstance.feedUrl}" target="_blank">${fieldValue(bean: blogInstance, field: "feedUrl")}</a></td>

                <td>${fieldValue(bean: blogInstance, field: "pollFrequency")}</td>

                <td>${fieldValue(bean: blogInstance, field: "status")}</td>

                <td>${fieldValue(bean: blogInstance, field: "lastError")}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${blogInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
