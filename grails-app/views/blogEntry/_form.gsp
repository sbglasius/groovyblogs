<%@ page import="org.groovyblogs.BlogEntry" %>



<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'language', 'error')} ">
    <label for="language">
        <g:message code="blogEntry.language.label" default="Language"/>

    </label>
    <g:textField name="language" value="${blogEntryInstance?.language}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'link', 'error')} required">
    <label for="link">
        <g:message code="blogEntry.link.label" default="Link"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="link" required="" value="${blogEntryInstance?.link}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'hash', 'error')} ">
    <label for="hash">
        <g:message code="blogEntry.hash.label" default="Hash"/>

    </label>
    <g:textField name="hash" value="${blogEntryInstance?.hash}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'blog', 'error')} required">
    <label for="blog">
        <g:message code="blogEntry.blog.label" default="Blog"/>
        <span class="required-indicator">*</span>
    </label>
    <g:select id="blog" name="blog.id" from="${org.groovyblogs.Blog.list()}" optionKey="id" required="" value="${blogEntryInstance?.blog?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'dateAdded', 'error')} required">
    <label for="dateAdded">
        <g:message code="blogEntry.dateAdded.label" default="Date Added"/>
        <span class="required-indicator">*</span>
    </label>
    <g:datePicker name="dateAdded" precision="day" value="${blogEntryInstance?.dateAdded}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'description', 'error')} required">
    <label for="description">
        <g:message code="blogEntry.description.label" default="Description"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="description" required="" value="${blogEntryInstance?.description}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'hitCount', 'error')} required">
    <label for="hitCount">
        <g:message code="blogEntry.hitCount.label" default="Hit Count"/>
        <span class="required-indicator">*</span>
    </label>
    <g:field name="hitCount" type="number" value="${blogEntryInstance.hitCount}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'title', 'error')} required">
    <label for="title">
        <g:message code="blogEntry.title.label" default="Title"/>
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="title" required="" value="${blogEntryInstance?.title}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: blogEntryInstance, field: 'visits', 'error')} ">
    <label for="visits">
        <g:message code="blogEntry.visits.label" default="Visits"/>

    </label>

</div>

