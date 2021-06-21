<html>
<head>
    <meta name="layout" content="main"/>
    <title>Internal server error</title>
</head>

<body>
    <div class="row">
        <div class="col-md-3">
            <asset:image src="500.jpg" style="width: 100%"/>
        </div>
        <div class="col-md-9">
            <h1>Snap.... 500'ish</h1>
            <h4>Something inside broke! You didn't do it - we did.</h4>
            <p>If you feel like talking to someone about it, just ping us at info<i class="fa fa-at"></i>groovyblogs.org... </p>
            <p>Sorry about the trouble...</p>
        </div>
    </div>

<g:if env="development">
    <h1>Grails Runtime Exception</h1>

    <h2>Error Details</h2>

    <div class="message">
        <strong>Message:</strong> ${exception.message?.encodeAsHTML()} <br/>
        <strong>Caused by:</strong> ${exception.cause?.message?.encodeAsHTML()} <br/>
        <strong>Class:</strong> ${exception.className} <br/>
        <strong>At Line:</strong> [${exception.lineNumber}] <br/>
        <strong>Code Snippet:</strong><br/>

        <div class="snippet">
            <g:each var="cs" in="${exception.codeSnippet}">
                ${cs?.encodeAsHTML()}<br/>
            </g:each>
        </div>
    </div>

    <h2>Stack Trace</h2>

    <div class="stack" style="font-family: 'Courier New', Courier, monospace">
        ${exception.stackTraceText?.encodeAsHTML()}
    </div>
</g:if>

</body>
</html>