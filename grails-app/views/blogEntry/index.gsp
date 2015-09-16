<%@ page import="org.groovyblogs.BlogEntry" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blogEntry.label', default: 'BlogEntry')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
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
        <g:each in="${blogEntryList}" status="i" var="blogEntry">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                <td><g:link action="show" id="${blogEntry.id}">${fieldValue(bean: blogEntry, field: "id")}</g:link></td>


                <td>${fieldValue(bean: blogEntry.blog, field: "title")}</td>

                <td><g:formatDate date="${blogEntry.dateAdded}"/></td>

                <td><a href="${blogEntry.link}" target="_blank">link ${blogEntry.groovyRelated}</a></td>
                <td><iframe src="${blogEntry.link}" width="400px" height="200px"></iframe></td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <p:paginate total="${blogEntryCount ?: 0}"/>
    </div>
</div>
</body>
</html>
