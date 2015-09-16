<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>

<div id="create-user" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>
    <g:hasErrors bean="${user}">
        <ul class="errors" role="alert">
            <g:eachError bean="${user}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: user, action: 'save']">
            <f:all bean="${user}"/>
            <g:submitButton name="create" class="btn btn-primary" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </g:form>
</div>
</body>
</html>
