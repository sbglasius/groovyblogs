<html>
<head>
    <meta name='layout' content='main'/>
    <title>Forgotten Password</title>
</head>

<body>

<div class="login-spacer">

</div>

<div class="modal login" id="login" data-backdrop="static">
    <g:form action='forgottenPassword' method='POST' class='cssform' autocomplete='off'>

        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4 class="modal-title">Forgotten password</h4>
                </div>

                <div class="modal-body">
                    <p>So you forgot your password? Let's see if we can help!</p>
                    <tmpl:/templates/message/>

                    <div class="row pad-bottom-10">
                        <div class="col-xs-12">
                            <label for='identity'>Enter your username or email address:</label>
                        </div>

                        <div class="col-xs-12">
                            <input type='text' class='form-control' name='identity' id='identity'/>
                        </div>
                    </div>

                </div>

                <div class="modal-footer">
                    <input type='submit' id="submit" class="btn btn-default" value='Reset'/>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </g:form>
</div><!-- /.modal -->


<asset:script>
    $(function () {
        $('#username').focus();
        $('#login').modal('show')
    });
</asset:script>
</body>
</html>
