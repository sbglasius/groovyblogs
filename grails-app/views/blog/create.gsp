<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'blog.label', default: 'Blog')}"/>
    <title><g:message code="default.create.label" args="[entityName]"/></title>
</head>

<body>

<div id="create-blog" class="content scaffold-create" role="main">
    <h1><g:message code="default.create.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>
    <g:hasErrors bean="${blogInstance}">
        <ul class="errors" role="alert">
            <g:eachError bean="${blogInstance}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
            </g:eachError>
        </ul>
    </g:hasErrors>
    <g:form url="[resource: blogInstance, action: 'save']" class="">
            <f:all bean="${blogInstance}"/>
            <g:submitButton name="create" class="btn btn-default" value="${message(code: 'default.button.create.label', default: 'Create')}"/>
    </g:form>
</div>
</body>
</html>
