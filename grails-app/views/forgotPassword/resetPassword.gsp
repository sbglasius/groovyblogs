<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
</head>

<body>

<div class="login-spacer">

</div>

<div class="modal login" id="reset" data-backdrop="static">

    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title"><g:message code="springSecurity.reset-password.header"/></h4>
            </div>

            <g:form action='resetPassword' method='POST' id='resetPassword' class='form' autocomplete='off'>
                <div class="modal-body">

                    <tmpl:/templates/message/>
                    <div class="form-horizontal">
                        <f:with bean="command">
                            <g:hiddenField name="username" value="${command.username}"/>
                            <g:hiddenField name="token" value="${command.token}"/>

                            <f:field property="newPassword"/>
                            <br/>
                            <f:field property="repeatPassword"/>
                        </f:with></div>
                </div>

                <div class="modal-footer">
                    <input type='submit' id="submit" class="btn btn-default" value='${message(code: "springSecurity.resetPassword.button")}'/>
                </div>
            </g:form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<asset:script>
    $(function () {
        $('#reset').modal('show')
    });
</asset:script>
</body>
</html>
