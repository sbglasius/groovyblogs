<div class="row ${invalid ? 'has-error' : ''} pad-bottom-5">
    <div class="col-sm-4" >
    <label for="${field}" class="control-label">${label} <g:if test="${required}">*</g:if></label>
    </div>
    <div class="col-sm-8">
        ${raw(widget)}
        <g:if test="${errors}">
            <g:each in="${errors}" var="error">
                <span class="help-block"><g:message error="${error}"/> </span>
            </g:each>
        </g:if>
    </div>
</div>
