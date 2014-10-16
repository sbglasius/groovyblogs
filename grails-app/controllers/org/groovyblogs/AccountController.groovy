package org.groovyblogs
import grails.plugin.springsecurity.annotation.Secured
import groovy.xml.MarkupBuilder

import javax.servlet.http.Cookie

@Secured(['ROLE_USER'])
class AccountController {

    def springSecurityService

    FeedService feedService

    private User getCurrentUser() {
        springSecurityService.currentUser as User
    }

    static defaultAction = 'edit'

    def edit() {
        def account = getCurrentUser()
        if (!account) {
            flash.message = "Account not found with id ${account.id}" // TODO this is going fail with NPE
            redirect(action: 'list')
            return
        }

        [account: account]
    }

    def update(Long id, String password) {
        def account = User.get(id)
        if (account.id != getCurrentUser()?.id) {
            flash.message = "Account not found with id ${id}"
            redirect(action: 'edit', id: id)
            return
        }

        //account.properties = params
        bindData(account, params, ['password'])
        if (password) {
            account.password = password.encodeAsSHA1Bytes().encodeBase64()
        }

        if (account.save()) {
            flash.message = "Updated successfully"
            redirect(action: 'edit', model: [account: account])
            return
        }

        render(view: 'edit', model: [account: account])
    }

    @Secured(['permitAll'])
    def signup() {
        def account = new User()
        account.properties['username', 'password', 'email'] = params
        [account: account]
    }

    @Secured(['permitAll'])
    def register(String username, String password, String email) {
        def account = new User(
            username: username,
            password: password,
            email: email,
            registered: new Date(),
            status: "active")
        if (account.save(flush: true)) {
            redirect(action: 'edit')
            //render(view: 'addfeed', model: ['account':account ])
            return
        }

        account.password = params.password
        render(view: 'signup', model: [account: account])
    }


    def deleteFeed(Long id) {
        def blog = Blog.get(id)
        if (!blog) {
            flash.message = "Blog not found with id ${id}"
            redirect(action: 'edit')
            return
        }

        if (blog.accountId != getCurrentUser()?.id) {
            flash.message = "You don't have rights to delete that blog"
            redirect(action: 'edit')
            return
        }

        blog.delete()
        flash.message = "Successfully deleted blog ${blog.title}"
    }

    def addFeed() {

        def feedUrl = params.feedUrl
        log.info("Adding Feed: [$feedUrl]")
        if (feedUrl) {

            def blog = new Blog()
            blog.feedUrl = params.feedUrl
            def feedInfo = feedService.getFeedInfo(params.feedUrl)

            blog.title = feedInfo.title ? feedInfo.title : ""
            blog.title = blog.title.length() > 250 ? blog.title[0..249] : blog.title
            blog.description = feedInfo.description ? feedInfo.description : ""
            blog.description = blog.description.length() > 250 ? blog.description[0..249] : blog.description

            def account = getCurrentUser()
            blog.account = account
            if (blog.validate()) {
                blog.save()
                if (grailsApplication.config.feeds.moderate) {
                    blog.status = BlogStatus.PENDING
                    try {
                        sendMail {
                            to grailsApplication.config.feeds.moderator_email
                            subject "groovyblogs: Feed approval for ${feedInfo.title}"
                            body """
                        <p>
                        Request to approve URL: ${feedInfo.title} at url <a href="${params.feedUrl}">${params.feedUrl}</a>
                        </p>
                        <p>
                        <a href="http://www.groovyblogs.org/account/approveFeed/${blog.id}?password=${grailsApplication.config.feeds.approval_password}">Approve</a>

                        for ${blog.account.email} or

                        <a href="http://www.groovyblogs.org/account/removeFeed/${blog.id}?password=${grailsApplication.config.feeds.approval_password}">Delete</a>
                        </p>

                    """
                        }

                    } catch (Exception e) {
                        log.error "Could not add feed", e
                    }
                    flash.message = "Successfully added new feed: ${feedInfo.title}. Your Feed needs to be approved by a moderator to become visible"

                } else {
                    feedService.updateFeed(blog)
                    blog.status = BlogStatus.ACTIVE
                    flash.message = "Successfully added new feed: ${feedInfo.title}"
                }
            } else {
                flash.message = "Error adding feed: ${blog?.errors}"
            }


        } else {
            flash.message = "Could not determine feed url"
        }
        redirect(action: 'edit')
    }


    def updateFeed() {
        def blog = Blog.get(params.id)

        if (blog && blog.status == BlogStatus.ACTIVE) {
            log.info("Updating Feed: [${blog?.feedUrl}]")
            feedService.updateFeed(blog)
            flash.message = "Successfully updated ${blog.title}"
        } else {
            flash.message = "Unapproved blog, or could not determine blog id"
        }
        redirect(action: 'edit')

    }

    def testFeed() {

        def feedUrl = params.feedUrl
        log.debug("Testing Feed: [$feedUrl]")
        if (feedUrl) {
            def feedInfo = feedService.getFeedInfo(feedUrl)
            log.debug("Returned $feedInfo.title $feedInfo.description $feedInfo.type")
            def writer = new StringWriter()
            def html = new MarkupBuilder(writer)

            // Could do all this directly in a render() call but it's harder to debug
            html.div {
                div(id: "iconDeets") {
                    p(style: 'margin-top: 3px; margin-bottom: 3px') {

                        img(src: "../images/accept.png",
                                alt: "This is a groovy related post")
                        span("Groovy/Grails Post ")
                        img(src: "../images/cancel.png",
                                alt: "Not a groovy related post",
                                style: "margin-left: 5px;")
                        span("Non Groovy/Grails Post (won't be aggregated) ")
                    }
                }

                div(id: "blogInfo") {
                    div(id: "blogTitle") { p(feedInfo?.title) }
                    div(id: "blogDesc") { p(feedInfo?.description) }
                    div(id: "blogType") { p(feedInfo?.type) }
                    div(id: "blogEntries") {
                        for (e in feedInfo?.entries) {
                            div(class: "blogEntry") {
                                div(class: "blogEntryTitle") {

                                    p {
                                        def isGroovyRelated = new BlogEntry(title: e.title, description: e.description).isGroovyRelated()
                                        img(src:
                                                isGroovyRelated ? "../images/accept.png" : "../images/cancel.png",
                                                alt:
                                                        isGroovyRelated ? "This is a groovy related post" : "Not a groovy related post",
                                        )

                                        span(e?.title)
                                    }
                                }
                                div(class: "blogEntryDesc") { p(e?.summary) }
                            }
                        }

                    }
                }

            }

            log.debug(writer.toString())

            render(writer.toString())


        } else {
            render "You need to provide a URL for me"
        }

    }

    @Secured(['ROLE_ADMIN'])
    def approveFeed() {
        Blog blog = Blog.get(params.id)
        if (blog) {
            blog.status = BlogStatus.ACTIVE
            log.warn "Approving blog: ${blog.title} - ${blog.id}"
            render "<h1>Approval all good for ${blog.title} - ${blog.id}</h1>"
        } else {
            render "<h1>Missing blog</h1>"
        }
    }

    @Secured(['ROLE_ADMIN'])
    def removeFeed() {

        Blog blog = Blog.get(params.id)
        if (blog) {
            blog.delete()
            log.warn "Deleting blog: ${blog.title} - ${blog.id}"
            render "<h1>Delete all good for ${blog.title} - ${blog.id}</h1>"
        } else {
            render "<h1>Missing blog</h1>"
        }

    }

    def preferredLang() {

        def prefLang = params.id
        if (prefLang == "en") {
            def newCookie = new Cookie("lang", "en")
            newCookie.path = '/'
            newCookie.maxAge = 60 * 60 * 24 * 365 * 5 // 5 years is plenty
            response.addCookie(newCookie)
            flash.message = "Setting preferred language to English"
        } else {
            def langCookie = request.cookies.find { cookie -> cookie.name == "lang" }
            if (langCookie) { // remove it
                langCookie.maxAge = 0
                langCookie.path = '/'
                response.addCookie(langCookie)
                flash.message = "Removing preferred language"
            }
        }
        redirect(uri: "/")

    }


}
