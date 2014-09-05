<div class="row pad-bottom-10 ${entry.language?.startsWith('en') ? 'english' : 'nonenglish'}">
    <div class="col-md-2">
        <g:link controller="entries" action="jump" id="${entry.id}" class="thumbnail img-thumbnail" data-thumbnail="${createLink(controller: 'thumbnail', action: 'show', id: entry.id)}">
                <i class="thumbnail-waiting fa fa-circle-o-notch fa-spin fa-3x"></i>
        </g:link>
    </div>

    <div class="col-md-10">
        <div class="row">
            <div class="col-md-12">
                <h3><g:link controller="entries" action="jump" id="${entry.id}">
                    ${entry.title}
                </g:link></h3>
                <g:translate entry="${entry}"/>

            </div>

            <div class="col-md-12">
                <g:link controller="blog" action="show" id="${entry.blog.id}" style="">${entry.blog.title}</g:link>
                [ ${entry.hitCount} clicks ] -
                <g:dateFromNow date="${entry.dateAdded}"/>

            </div>

            <div class="col-md-12">
                <g:summariseEntry description="${entry.description}"/>
            </div>
        </div>

    </div>
</div>