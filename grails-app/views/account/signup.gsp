
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <g:javascript library="prototype" />
         <title>Create Account</title>         
    </head>
    <body>
        <div id="content">
            <div class="well well-lg">
           <h1>Signup</h1>
                <p>Running a blog related to the Groovy ecosystem? Start by signing up here:</p>
           <g:form action="register" method="post" >
               <f:with bean="${command}">
                   <f:field property="username"/>
                   <f:field property="password"/>
                   <f:field property="email"/>
                   <f:field property="recaptcha">
                   <recaptcha:ifEnabled>
                       <recaptcha:recaptcha theme="blackglass"/>
                   </recaptcha:ifEnabled>
                   </f:field>

               </f:with>
               <div class="text-right">
                   <g:submitButton class="btn btn-default" name="submit" value="Create account"/>
               </div>
            </g:form>
            </div>
            <div id="feedresults"></div>
            
        </div>
    </body>
</html>
            