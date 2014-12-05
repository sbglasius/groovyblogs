<%@ page import="org.groovyblogs.BlogEntry" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blogEntry.label', default: 'BlogEntry')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<a href="#list-blogEntry" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="list-blogEntry" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <table class="table table-bordered">
        <thead>
        <tr>

            <g:sortableColumn property="id" title="${message(code: 'blogEntry.id.label', default: 'Id')}"/>



            <th>Blog</th>

            <g:sortableColumn property="dateAdded" title="${message(code: 'blogEntry.dateAdded.label', default: 'Date Added')}"/>

            <th>Link</th>
            <g:sortableColumn property="description" title="${message(code: 'blogEntry.description.label', default: 'Description')}"/>

        </tr>
        </thead>
        <tbody>
        <g:each in="${blogEntryInstanceList}" status="i" var="blogEntryInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${blogEntryInstance.id}">${fieldValue(bean: blogEntryInstance, field: "id")}</g:link></td>


                <td>${fieldValue(bean: blogEntryInstance.blog, field: "title")}</td>

                <td><g:formatDate date="${blogEntryInstance.dateAdded}"/></td>

                <td><a href="${blogEntryInstance.link}" target="_blank">link ${blogEntryInstance.groovyRelated}</a></td>
                <td><iframe src="${blogEntryInstance.link}" width="400px" height="200px"></iframe></td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${blogEntryInstanceCount ?: 0}"/>
    </div>
</div>
</body>
</html>
