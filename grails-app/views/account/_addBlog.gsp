<div class="well well-lg">
    <p>
        groovyblogs.org hosts blog entries related to the <a href="http://groovy.codehaus.org">Groovy</a>
        ecosystem, including <a href="http://www.grails.org">Grails</a>, <a href="http://gradleware.org">Gradle</a> and others.</p>

    <p>If your feed is not related to these technologies, it will be filtered by the engine and will not display.</p>


    <p>Use the <strong>Test Feed</strong> button to see what will and won't get aggregated.</p>

    <p>All feeds are subject to moderation, so your blog might not appear right away.
    </p>
</div>
<g:form action="addFeed" method="post">
    <f:with bean="blog">
        <f:field property="feedUrl"/>
    </f:with>
    <div class="text-right">
        <button id="test-feed" class="btn btn-info">Test Feed</button>
        <g:submitButton name="submit" value="Add Feed" class="btn btn-success"/>
    </div>
</g:form>
<asset:script>
    $('#test-feed').on('click',function(event) {
        event.preventDefault();
        $('#feed-modal').load('${createLink(action: 'testFeedModal')}', function() {
            $(this).on('shown.bs.modal', function() {
                $('#feed-result').load('${createLink(action: 'testFeed')}', {feedUrl: $('input[name="feedUrl"]').val()}, function() {
                    $('#modal-add-feed').removeClass('disabled').on('click', function() {
                        $('input[name="submit"]').trigger('click');
                    });
                })
            }).modal('show');
        })
    });
</asset:script>
<div id="feed-modal" class="modal fade">
</div>