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

                    <tmpl:/templates/message/>

                    <div class="row pad-bottom-10">
                        <div class="col-xs-12">
                            <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
                        </div>

                        <div class="col-xs-12">
                            <input type='text' class='form-control' name='username' id='username'/>
                        </div>
                    </div>

                    <div class="row pad-bottom-10">

                        <div class=" col-xs-12">
                            <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
                        </div>

                        <div class="col-xs-12">
                            <input type='password' class='form-control' name='password' id='password'/>
                        </div>
                        <div class="col-xs-12 text-right">
                            <g:link controller="forgotPassword"><small>Forgot password?</small></g:link>

                        </div>

                    </div>
                </div>

                <div class="modal-footer">

                    <g:link controller="entries" class="btn btn-warning">Cancel</g:link>
                    <input type='submit' id="submit" class="btn btn-primary" value='${message(code: "springSecurity.login.button")}'/>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </form>
</div><!-- /.modal -->


<asset:script>
    $(function () {
        $('#username').focus();
        $('#login').modal('show')
    });
</asset:script>
</body>
</html>
