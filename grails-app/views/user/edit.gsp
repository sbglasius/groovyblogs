<%@ page import="org.groovyblogs.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>
<a href="#edit-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>


<div id="edit-user" class="content scaffold-edit" role="main">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>
    <g:hasErrors bean="${user}">
        <ul class="errors" role="alert">
            <g:eachError bean="${user}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: user, action: 'update']" method="PUT">
        <g:hiddenField name="version" value="${user?.version}"/>
            <f:all bean="${user}"/>
            <g:actionSubmit class="save" action="btn btn-primary" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    </g:form>
</div>
</body>
</html>
