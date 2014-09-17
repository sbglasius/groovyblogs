<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
</head>

<body>

<div class="login-spacer">

</div>
<div class="modal login" id="login" data-backdrop="static">
    <form action='${postUrl}' method='POST' id='loginForm' class='cssform' autocomplete='off'>

        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title"><g:message code="springSecurity.login.header"/></h4>
                </div>

                <div class="modal-body">

                    <g:if test='${flash.message}'>
                        <div class="alert alert-danger">
                            <div>${flash.message}</div>
                        </div>
                    </g:if>

                    <div class="row pad-bottom-10">
                        <div class="col-xs-12">
                            <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
                        </div>

                        <div class="col-xs-12">
                            <input type='text' class='form-control' name='j_username' id='username'/>
                        </div>
                    </div>

                    <div class="row pad-bottom-10">

                        <div class=" col-xs-12">
                            <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
                        </div>

                        <div class="col-xs-12">
                            <input type='password' class='form-control' name='j_password' id='password'/>
                        </div>

                    </div>
                </div>

                <div class="modal-footer">
                    <input type='submit' id="submit" class="btn btn-default" value='${message(code: "springSecurity.login.button")}'/>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </form>
</div><!-- /.modal -->


<asset:script>
    $(function () {
        $('#j_username').focus();
        $('#login').modal('show')
        console.debug($('#login'))
    });
</asset:script>
</body>
</html>
