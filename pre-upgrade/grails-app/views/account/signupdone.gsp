
<html>
    <head>
         <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
         <meta name="layout" content="main" />
         <title>Sign Complete</title>         
    </head>
    <body>
        <div id="content">
           <h1>Signup Underway</h1>
           
           <g:hasErrors bean="${account}">
                <div class="errors">
                    <g:renderErrors bean="${account}" as="list" />
                </div>
           </g:hasErrors>
           <p>
           Hi ${account.userid}, thanks for registering with groovyblogs.org. We
           have sent a comfirmation email to <b>${account.email}</b> which contains
           an activation link for you to complete the process.
           </p>
           <p>
           Look forward to reading your Groovy and Grails blogs on groovyblogs.org.
           </p>
           <p>
           Glen Smith - groovyblogs.org
           </p>
        </div>
    </body>
</html>
            