
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <title>Login</title>         
    </head>
    <body>
        <div class="body">
           <h1>Login</h1>
 
           <g:form action="login" method="post" >
               <div class="dialog">
                <table>

                       
                       
                                  <tr class='prop'><td valign='top' class='name'><label for='userid'>Userid:</label></td><td valign='top' class='value ${hasErrors(bean:account,field:'username','errors')}'><input type='text' name='userid' value='${account?.userid}' /></td></tr>
                       
                                   <tr class='prop'><td valign='top' class='name'><label for='password'>Password:</label></td><td valign='top' class='value ${hasErrors(bean:account,field:'password','errors')}'><input type="password" name='password' value='${account?.password}' /></td></tr>
                       
                        
               </table>
               </div>
               <div class="buttons">
                     <span class="formButton">
                        <input type="submit" value="Login"></input>
                     </span>
               </div>
            </g:form>
            <p>
            <g:link action="forgottenPassword">Forgotten your password?</g:link>
            <g:link controller='account' action="signup">Need to Sign Up?</g:link>
            </p>
        </div>
    </body>
</html>
            