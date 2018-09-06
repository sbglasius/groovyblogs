<%@ page import="org.groovyblogs.Blog" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blog.label', default: 'Blog')}"/>
    <title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>

<div id="edit-blog" class="content scaffold-edit" role="main">
    <h1><g:message code="default.edit.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>
    <g:hasErrors bean="${blogInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${blogInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: blogInstance, action: 'update']" method="PUT">
        <g:hiddenField name="version" value="${blogInstance?.version}"/>
            <f:all bean="${blogInstance}"/>
            <g:actionSubmit class="btn btn-primary" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}"/>
    </g:form>
</div>
</body>
</html>
