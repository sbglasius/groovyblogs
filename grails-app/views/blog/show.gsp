<%@ page import="org.groovyblogs.Blog" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blog.label', default: 'Blog')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>
<a href="#show-blog" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>

<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>

<div id="show-blog" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
    </g:if>
    <div class="form-horizontal">
        <f:with bean="${blogInstance}">
            <f:display property="title"/>
            <f:display property="description"/>
            <f:display property="status"/>
            <f:display property="feedUrl"/>
            <f:display property="lastPolled"/>
            <f:display property="nextPoll"/>
            <f:display property="pollFrequency"/>
            <f:display property="errorCount"/>
            <f:display property="lastError"/>
            <f:display property="account.username"/>
            <f:display property="blogEntries">
                ${blogInstance.blogEntries?.size()}
            </f:display>
            <f:display property="registered"/>
        </f:with>
    </div>
    <g:form url="[resource: blogInstance, action: 'delete']" method="DELETE">
            <g:link class="btn btn-primary" action="edit" resource="${blogInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="btn btn-warning" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
    </g:form>
</div>
</body>
</html>
