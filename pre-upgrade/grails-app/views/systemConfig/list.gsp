

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'systemConfig.label', default: 'SystemConfig')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <tmpl:/templates/message/>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'systemConfig.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="settingName" title="${message(code: 'systemConfig.settingName.label', default: 'Setting Name')}" />
                        
                            <g:sortableColumn property="settingValue" title="${message(code: 'systemConfig.settingValue.label', default: 'Setting Value')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${systemConfigInstanceList}" status="i" var="systemConfigInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${systemConfigInstance.id}">${fieldValue(bean: systemConfigInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: systemConfigInstance, field: "settingName")}</td>
                        
                            <td>${fieldValue(bean: systemConfigInstance, field: "settingValue")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${systemConfigInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
