<g:if test="${constraints.inList}">
    <g:if test="${mandatory}">
        <g:select name="${property}" from="${constraints.inList}" value="${value}" class="form-control"/>
    </g:if>
    <g:else>
        <g:select name="${property}" from="${constraints.inList}" value="${value}" noSelection="['':'']" class="form-control"/>
    </g:else>
    <g:if test="${constraints.attributes?.select2}">
        <asset:script>
            $('select[name="${property}"]').select2();
        </asset:script>
    </g:if>
</g:if>
<g:else>
    <g:textField name="${property}" value="${value}" class="form-control"/>
</g:else>
