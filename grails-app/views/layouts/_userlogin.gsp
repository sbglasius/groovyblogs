<div class="col-xs-12">
    <div class="panel panel-info">
        <div class="panel-heading">
            <sec:ifNotLoggedIn>Login</sec:ifNotLoggedIn>
            <sec:ifLoggedIn>User Info</sec:ifLoggedIn>
        </div>

        <div class="panel-body">
            <sec:ifLoggedIn>
                <div class="row pad-bottom-5">


                    <div class="col-xs-12"><g:link controller="account" action="edit"><sec:username/></g:link></div>
                </div>

                <div class="row">
                    <div class="col-xs-12 text-right">
                        <g:form controller="logout" action="index">
                            <button class="btn btn-danger btn-xs">Logout</button>
                        </g:form>
                    </div>
                </div>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <form action='${createLink(uri: '/j_spring_security_check')}' method='POST' id='loginForm' class='login-box text-left' autocomplete='off'>
                    <div class="row">
                        <div class="col-xs-12 pad-bottom-5">
                            <input type='text' class='form-control' name='j_username' id='username'/>
                        </div>

                        <div class="col-xs-12  pad-bottom-5">
                            <input type='password' class='form-control' name='j_password' id='password'/>
                        </div>

                        <div class="col-xs-12 text-right">
                            <button class="btn btn-primary btn-xs">Login</button>
                        </div>
                        <div class="col-xs-12">
                            <g:link action="forgottenPassword"><small>Forgot password</small></g:link>
                        </div>
                        <div class="col-xs-12">
                            <g:link controller='account' action="signup"><small>Sign Up</small></g:link>
                        </div>

                    </div>
                </form>
            </sec:ifNotLoggedIn>
        </div>
    </div>
</div>
