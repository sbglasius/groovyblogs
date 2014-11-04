package org.groovyblogs

import com.megatome.grails.RecaptchaService
import grails.plugin.springsecurity.annotation.Secured
import groovy.xml.MarkupBuilder

import javax.servlet.http.Cookie

@Secured(['ROLE_USER'])
class AccountController {

    def springSecurityService

    def feedService
    def userService

    private User getCurrentUser() {
        springSecurityService.loadCurrentUser() as User
    }

    static defaultAction = 'edit'

    def edit() {
        editModel
    }

    def update(UpdateAccountCommand command) {
        if(!command.hasErrors()) {
            def status = userService.updateUser(currentUser, command)
            flash.message = g.message(message: status)
        }
        render(view: 'edit', model: [account: currentUser, command: command, blog: new Blog()])
    }

    def resendConfirm() {
        userService.sendConfirmEmail(currentUser)
        flash.message = "Check your email! our message could be in your spam folder..."
        redirect(action: 'edit')

    }

    @Secured(['permitAll'])
    def confirmEmail(TokenCommand command) {
        if(command.hasTokenError()) {
            flash.message = "That's not right... The token was not found. Remember the token only lives 24 hours. Perhaps you could try again."
        } else {
            userService.confirmEmail(command)
            flash.message = "Your email address was confirmed. Thank you."
        }
        if(currentUser) {
            redirect(action: 'edit')
        } else {
            redirect(controller: 'entries')
        }

    }

    @Secured(['permitAll'])
    def signup() {
        def command = new RegisterAccountCommand()
        bindData(command, params)
        [command: command]
    }
    RecaptchaService recaptchaService

    @Secured(['permitAll'])
    def register(RegisterAccountCommand command) {
        if(!recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)) {
            command.errors.rejectValue('recaptcha','recaptcha-not-valid')
        }
        if(command.hasErrors()) {
            println command.errors
            render(view: 'signup', model: [command: command])
        } else {
            if(userService.createAccount(command)) {
                flash.message = "Welcome to groovyblogs.org!"
                redirect(action: 'edit', params: [tab: 'newblog'])
            } else {
                command.errors.reject("Could not create your account. Contact info@groovyblogs.org and we will help you.")
                render(view: 'signup', model: [command: command])
            }
        }

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


    private LinkedHashMap<String, GroovyObjectSupport> getEditModel() {
        [account: getCurrentUser(), command: new UpdateAccountCommand(email: getCurrentUser().unconfirmedEmail ?: getCurrentUser().email), blog: new Blog()]
    }




}
