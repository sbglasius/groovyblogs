<g:hasErrors bean="${command}">
    <div class="alert alert-danger">
    <g:renderErrors bean="${command}" as="list"/>
    </div>
</g:hasErrors>
<g:form controller="account" action="update" method="post">
    <f:with bean="account">
        <input type="hidden" name="id" value="${account?.id}"/>
        <f:display property="username"/>
        <f:field property="name"/>
        <f:field property="twitter"/>
    </f:with>
    <f:with bean="command">
        <f:field property="newPassword"/>
        <f:field property="repeatPassword"/>
        <f:field property="email"/>
        <g:if test="${account.unconfirmedEmail}">
        <div class="row pad-bottom-5">
            <div class="col-sm-push-4 col-sm-8">
                <p class="form-control-static">
                    <small class="text-danger">This email is unconfirmed. Please check your email. If you can not find it or the link has expired,
                    <g:link action="resendConfirm">resend</g:link> it.</small>
                </p>
            </div>
        </div>

        </g:if>
    </f:with>
    <div class="buttons">
        <g:actionSubmit class="btn btn-primary" value="Update"/>
    </div>

</g:form>