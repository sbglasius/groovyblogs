<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="loading"/>
    <title>Loading blog</title>
</head>

<body>
<div class="groovyblogs-overlay">

    <div id="loading" class="loading-frame">
        <div class="centered text-center">
            <g:link uri="/" absolute="true"><asset:image src="groovyblogs-loading.png"/></g:link>
            <div>
                Loading <a href="${blogEntry.link}">${blogEntry.title}</a> <small>from ${blogEntry.blog.title}</small>
            </div>
        </div>
    </div>

    <div id="external-frame" class="content-frame" data-url="${blogEntry.link}">
        <iframe src="" id="content-frame"></iframe>
    </div>

    <div id="loaded-frame" class="loaded-frame">
        <div class="overlay">
            <button type="button" class="close" id="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>

            <div class="row">
                <div class="col-sm-12">
                    <asset:image src="groovyblogs-vertical.png" class="pull-right"/>

                    <div><a href="${blogEntry.link}">${blogEntry.title}</a> <small>from ${blogEntry.blog.title}</small></div>

                    <div class="extra-small">Views: ${blogEntry.hitCount}</div>
                </div>
            </div>
        </div>
    </div>
</div>
<asset:script>
        var extFrame = $('#external-frame').hide();
        var iFrame = $('#content-frame');
        var overlay = $('#loaded-frame .overlay');
        iFrame.on('load', function () {
            extFrame.fadeIn('slow', function () {
                $('#loading').remove();
                overlay.animate({'bottom': '+=60px'}, function() {
                    console.debug("---->",$('#close'));
                    $('#close').on('click', function () {
                        window.location.href = '${blogEntry.link}';
                    });
                })
            });
        }).attr('src', '${blogEntry.link}');
</asset:script>
</body>

</html>