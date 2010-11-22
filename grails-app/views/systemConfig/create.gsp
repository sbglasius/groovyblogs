


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'systemConfig.label', default: 'SystemConfig')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${systemConfigInstance}">
            <div class="errors">
                <g:renderErrors bean="${systemConfigInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="settingName"><g:message code="systemConfig.settingName.label" default="Setting Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: systemConfigInstance, field: 'settingName', 'errors')}">
                                    <g:textField name="settingName" value="${systemConfigInstance?.settingName}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="settingValue"><g:message code="systemConfig.settingValue.label" default="Setting Value" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: systemConfigInstance, field: 'settingValue', 'errors')}">
                                    <g:textField name="settingValue" value="${systemConfigInstance?.settingValue}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
