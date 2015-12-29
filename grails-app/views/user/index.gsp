<%@ page import="org.groovyblogs.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
    <title><g:message code="default.list.label" args="[entityName]"/></title>
</head>

<body>
<div id="list-user" class="content scaffold-list" role="main">
    <h1><g:message code="default.list.label" args="[entityName]"/></h1>
    <tmpl:/templates/message/>
    <table class="table table-striped">
        <thead>
        <tr>

            <g:sortableColumn property="username" title="${message(code: 'user.username.label', default: 'Username')}"/>


            <g:sortableColumn property="email" title="${message(code: 'user.email.label', default: 'Email')}"/>

            <g:sortableColumn property="lastLogin" title="${message(code: 'user.lastLogin.label', default: 'Last Login')}"/>

            <th><g:message code='user.status.label' default= 'Status'/></th>

            <th>Feed count</th>

        </tr>
        </thead>
        <tbody>
        <g:each in="${userList}" status="i" var="userInstance">
            <tr>

                <td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>


                <td>${fieldValue(bean: userInstance, field: "email")}</td>

                <td>${fieldValue(bean: userInstance, field: "lastLogin")}</td>

                <td>
                    <i class="fa ${userInstance.accountExpired ? ' fa-times-circle':'fa-dot-circle-o'}" title="${userInstance.accountExpired ? 'Account expired':'Account active'}" ></i>
                    <i class="fa ${userInstance.accountLocked ? ' fa-lock':'fa-unlock-alt'}" title="${userInstance.accountLocked ? 'Account locked':'Account unlocked'}" ></i>

                </td>

                <td>${userInstance.blogs?.size()}</td>

            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <b:paginate total="${userInstanceCount ?: 0}"/>
    </div>
    <div>
        <g:link action="create" class="btn btn-primary"><g:message code="default.new.label" args="[entityName]"/></g:link>
        <g:link action="cleanupUsers" params="${params}" class="btn btn-danger" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
            Remove users with no blogs</g:link></div>

</div>
</body>
</html>
