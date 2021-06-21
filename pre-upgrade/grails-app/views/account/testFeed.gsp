<g:if test="${blog}">
    <f:with bean="${blog}">
        <f:display property="title"/>
        <f:display property="description"/>
        <f:display property="type"/>
    </f:with>
    Entries:
    <small><i class="fa fa-thumbs-o-up text-success"></i> Is Groovy related. <i class="fa fa-thumbs-o-down text-danger"></i> Is <strong>not</strong> Groovy related.</small>
    <g:if test="${blog.blogEntries}">
            <g:each in="${blog.blogEntries}" var="blogEntry">
                <div>
                    <i class="fa fa-2x fa-thumbs-o-${blogEntry.groovyRelated ? 'up text-success':'down text-danger'} "></i>  ${blogEntry.title}
                </div>
            </g:each>
        <g:if test="${blog.blogEntries.every { !it.groovyRelated}}">
            <div class="alert alert-warning">
                None of the entries above has Groovy related content. If you add this feed, we wil probably not accept it, unless you publish some relevant content to it.
            </div>
        </g:if>
    </g:if>
    <g:else>
        <div class="alert alert-danger">There are no entries in that feed URL. If you add this feed, we will probably not accept it, unless you publish some relevant content to it.?</div>
    </g:else>
</g:if>
<g:else>
    <h4>Provide a feed URL otherwise it can not be tested.</h4>
</g:else>