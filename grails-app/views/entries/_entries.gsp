<g:each in="${entries}" var="entry">
    <g:render template="entry" model="[entry: entry, thumbnails: thumbnails]"/>
</g:each>
