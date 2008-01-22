
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <title>Forgotten Password</title>         
    </head>
    <body>
        <div id="content">
           <h1>Forgotten Password</h1>
           
           
           <p>
           If you've fogotten your password, we can generate a new one for you
           and email it to the address that you registered with us originally.
           All we need to get started is your groovyblogs.org user id.
           </p>
           <p>
           (PS. If you've forgotten your user id, you might want to look it
           up in your email archives. We emailed it to you in a message
           from groovyblogs.org)
           </p>
 
           <g:form action="forgottenPassword" method="post" >
               <div class="dialog">
                <table>

                       
                       
                                  <tr class='prop'><td valign='top' class='name'><label for='userid'>Userid:</label></td><td valign='top' class='value ${hasErrors(bean:account,field:'userid','errors')}'><input type='text' name='userid' value='${account?.userid}' /></td></tr>
                        
               </table>
               </div>
               <div class="buttons">
                     <span class="formButton">
                        <input type="submit" value="Reset Password"></input>
                     </span>
               </div>
            </g:form>
        </div>
    </body>
</html>
            