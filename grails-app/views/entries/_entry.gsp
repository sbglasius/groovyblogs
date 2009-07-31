<div class="entry ${entry.language?.startsWith('en') ? 'english' : 'nonenglish'}">
  <table>
    <tr>
    <g:if test="${thumbnails}">
      <td style="padding-right: 60px">
        <div id="pic">
          <a class="p1" href="<g:createLink controller='entries' action='jump' id='${entry.id}'/>" title="thumbnail image">
            <img src="<g:createLink controller='thumbnail' action='show' id='${entry.id}'/>" alt="No Image Available" onmouseover="document.getElementById('large-${entry.id}').src = '<g:createLink controller='thumbnail' action='showLarge' id='${entry.id}'/>'" />
            <img id="large-${entry.id}" src="" alt="Loading Image..." class="large"  />
          </a>
        </div>
      </td>
    </g:if>
    <td valign="top" style="padding-top: 1em">
      <div class="entryTitle">
        <g:link controller="entries" action="jump" id="${entry.id}">
          ${entry.title}
        </g:link>
        <g:translate entry="${entry}"/>
      </div>
      <div class="entryDetails">
        <g:link controller="blog" action="show" id="${entry.blog.id}" style="">${entry.blog.title}</g:link>
					 [ ${entry.hitCount} clicks ] -
        <g:dateFromNow date="${entry.dateAdded}"/>
      </div>
      <div class="entrySummary">
        <g:summariseEntry description="${entry.description}"/>
      </div>
    <g:if test="${entry.info}">

      <div class="entryInfo">
        ${entry.info}
      </div>

    </g:if>
    </td>
    </tr></table>

</div>