<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${pageTitle}</title>

    %{--
        // TODO: Make language selection work again
        <g:if test="${request.cookies.find { cookie -> cookie.name == 'lang' }}">
            <style>
            .nonenglish {
                visibility: hidden;
                height: 0px;
            }
            </style>
        </g:if>
    --}%
</head>

<body>
<div class="body">
    <g:if test="${days}">
        <g:form action="popular" method="get" class="form-inline ">
            <h1>Popular Entries (last <g:select class="form-control " name="days" from="${selectableDays}" value="${days}" optionKey="key" optionValue="value"/>)</h1>
            <asset:script>
                $('select[name="days"]').on('change', function() {
                    $(this).closest('form').submit();
                });
            </asset:script>
        </g:form>
    </g:if>
    <g:else>
        <h1>${pageTitle}</h1>
    </g:else>
    <tmpl:/templates/message/>
    <div id="entries">
    </div>

    <div class="row">
        <div class="col-xs-12 pad-bottom-10">
            <button id="load" class="btn btn-info max-width" data-loading-text="Loading...">Load more</button>
        </div>
    </div>


    <asset:script>
        $(function() {
            function load(page) {
                var btn = $('#load');
                btn.text('Load more').button('loading');
                $.get('${createLink(action: actionName + 'Next')}', {days: ${days ?: 7}, page: page})
                .done(function(data, textStatus, jqXHR) {
                    btn.data('page', page);
                    $('#entries').append(data);
                    $('*[data-thumbnail]').fetchThumbnail();
                    btn.button('reset');
                    if(jqXHR.status != 200) {
                        btn.text('The end - no more entries');
                    }
                }).fail(function( jqXHR, textStatus) {
                    btn.button('reset').text('Error loading feed - try again')
                });
            }

        var btn = $('#load');
        btn.data('page',0).on('click', function() {
            var page = btn.data('page')+1;
            load(page);
        });
        load(0);
        });
    </asset:script>

</div>
</body>
</html>
