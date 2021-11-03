<%@ page import="org.groovyblogs.BlogEntry" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blogEntry.label', default: 'BlogEntry')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<div id="show-blogEntry" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <f:with bean="blogEntryInstance">
        <f:display property="title"/>
        <f:display property="link"/>
        <f:display property="dateAdded"/>
        <f:display property="hitCount"/>
        <f:display property="description">
            <iframe src="${blogEntryInstance.link}" width="800px" height="800px">
        </f:display>

    </f:with>
    <g:form url="[resource: blogEntryInstance, action: 'delete']" method="DELETE">
        <fieldset class="buttons">
            <g:link class="edit" action="edit" resource="${blogEntryInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
