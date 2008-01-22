
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
          <meta name="layout" content="main" />
         <title>Show Account</title>
    </head>
    <body>
        <div id="nav-section">
            <li class="youarehere"><g:link action="list">Account List</g:link></li>
            <li class="youarehere"><g:link action="create">New Account</g:link></li>
        </div>
        <div id="content">
           <h1>Show Account</h1>
           
           <div class="dialog">
                 <table>
                   
                   
                        <tr class="prop">
                              <td valign="top" class="name">Userid:</td>
                              
                                    <td valign="top" class="value">${account.userid}</td>
                              
                        </tr>
                   
                  
                   
                        <tr class="prop">
                              <td valign="top" class="name">Email:</td>
                              
                                    <td valign="top" class="value">${account.email}</td>
                              
                        </tr>

                   
                 </table>
           </div>
           <div class="buttons">
               <g:form controller="account">
                 <input type="hidden" name="id" value="${account?.id}" />
                 <span class="button"><g:actionSubmit value="Edit" /></span>
                 <span class="button"><g:actionSubmit value="Delete" /></span>
               </g:form>
           </div>
        </div>
    </body>
</html>
            