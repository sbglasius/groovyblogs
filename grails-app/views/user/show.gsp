<%@ page import="org.groovyblogs.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.show.label" args="[entityName]"/></title>
</head>

<body>

<div id="show-user" class="content scaffold-show" role="main">
    <h1><g:message code="default.show.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>
    <f:with bean="${userInstance}">
        <f:display property="username"/>
        <f:display property="email"/>
        <f:display property="status"/>
        <f:display property="accountExpired"/>
        <f:display property="accountLocked"/>
        <f:display property="passwordExpired"/>
        <f:display property="blogs">
            <ul>
                <g:each in="${value}" var="blog">
                    <li><g:link controller="blog" action="show" id="${blog.id}">${blog.title} <small>${blog.status}</small></g:link></li>
                </g:each>
            </ul>
        </f:display>
        <f:display property="enabled"/>
        <f:display property="registered"/>
        <f:display property="lastLogin"/>
    </f:with>
    <g:form url="[resource: userInstance, action: 'delete']" method="DELETE">
        <fieldset class="buttons">
            <g:link class="btn btn-primary" action="edit" resource="${userInstance}"><g:message code="default.button.edit.label" default="Edit"/></g:link>
            <g:actionSubmit class="btn btn-warning" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}"
                            onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
        </fieldset>
    </g:form>
</div>
</body>
</html>
