<div class="row pad-bottom-10 ${entry.language?.startsWith('en') ? 'english' : 'nonenglish'}">
    <div class="col-lg-2 col-md-3 col-sm-4 col-xs-3">
        <g:link controller="entries" action="jump" id="${entry.id}" class="thumbnail img-thumbnail" elementId="thumbnail-${entry.id}" data-thumbnail="${createLink(controller: 'thumbnail', action: 'show', id: entry.id)}">
            <i class="thumbnail-waiting fa fa-circle-o-notch fa-spin"></i>
        </g:link>
    </div>

    <div class="col-lg-10 col-md-9 col-sm-8 col-xs-9">
        <div class="row">
            <div class="col-md-12">
                <h4><g:link controller="entries" action="jump" id="${entry.id}">
                    ${entry.title}
                    <sec:ifNotGranted roles="['ROLE_ADMIN']">
                        <small>
                            ${entry.blog?.title}
                        </small>
                    </sec:ifNotGranted>
                </g:link></h4>
                <g:translate entry="${entry}"/>

                <h5>
                    <sec:ifAllGranted roles="['ROLE_ADMIN']">
                    <g:link controller="blog" action="show" id="${entry.blog.id}" style="">${entry.blog.title}</g:link>
                    </sec:ifAllGranted>
                    <small>
                        ${entry.hitCount} click${entry.hitCount != 1 ? 's':''}, added
                        <g:dateFromNow date="${entry.dateAdded}"/>
                    </small>
                </h5>

            </div>

            <div class="col-md-12">
                <g:summariseEntry description="${entry.description}"/>
            </div>
        </div>

    </div>
</div>
