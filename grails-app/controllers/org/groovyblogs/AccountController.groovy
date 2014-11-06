package org.groovyblogs

import com.megatome.grails.RecaptchaService
import grails.plugin.springsecurity.annotation.Secured

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Secured(['ROLE_USER'])
class AccountController {

    def springSecurityService

    def feedService
    def userService
    RecaptchaService recaptchaService

    static defaultAction = 'edit'

    def edit() {
        editModel
    }

    def update(UpdateAccountCommand command) {
        if (!command.hasErrors()) {
            def status = userService.updateUser(currentUser, command)
            flash.message = status.collect { g.message(message: it) }.join(' ')
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
        if (command.hasTokenError()) {
            flash.message = "That's not right... The token was not found. Remember the token only lives 24 hours. Perhaps you should try again."
        } else {
            userService.confirmEmail(command)
            flash.message = "Your email address was confirmed. Thank you."
        }
        if (currentUser) {
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

    @Secured(['permitAll'])
    def register(RegisterAccountCommand command) {
        if (!recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)) {
            command.errors.rejectValue('recaptcha', 'recaptcha-not-valid')
        }
        if (command.hasErrors()) {
            println command.errors
            render(view: 'signup', model: [command: command])
        } else {
            if (userService.createAccount(command)) {
                flash.message = "Welcome to groovyblogs.org!"
                redirect(action: 'edit', fragment: 'newblog')
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

        if (blog.accountId != currentUser?.id) {
            flash.message = "You don't have rights to delete that blog"
            redirect(action: 'edit')
            return
        }

        blog.delete()
        flash.message = "Successfully deleted blog ${blog.title}"
    }


    def addFeed(String feedUrl) {

        log.info("Adding Feed: [$feedUrl]")
        if (feedUrl) {

            Blog blog = feedService.createBlog(feedUrl, currentUser)
            if (blog.validate() && feedService.saveBlog(blog)) {
                flash.message = "Successfully added new feed: ${blog.title}. ${blog.status != BlogStatus.ACTIVE ? 'Your blog needs moderation before it becomes active.' : ''}"
            } else {
                flash.message = "Error adding feed. ${blog.errors.hasFieldErrors('feedUrl') ? 'This feed already exists in Groovy Blogs' : ''}"
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


    def testFeedModal() {

    }

    def testFeed(String feedUrl) {

        log.debug("Testing Feed: [$feedUrl]")
        if (feedUrl) {
            def blog = feedService.testFeed(feedUrl)
            log.debug("Returned blog title: $blog.title description: $blog.description, type: $blog.type, related: ${blog.blogEntries?.count { it.groovyRelated }}, non-related:  ${blog.blogEntries?.count { !it.groovyRelated }}  ")
            [blog: blog]
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
            [:]
        }
    }

    @Secured(['ROLE_MODERATOR'])
    def approveFeed(Blog blog) {
        if (blog) {
            blog.status = BlogStatus.ACTIVE
            log.warn "Approving blog: ${blog.title} - ${blog.id}"
            flash.message = "Approved blog: ${blog.title} - ${blog.id}"
        } else {
            flash.message = "Blog with id ${params.id} was not found..."
        }
        redirect(controller: 'entries', action: 'recent')
    }

    @Secured(['ROLE_MODERATOR'])
    def removeFeed(Blog blog) {

        if (blog) {
            blog.delete(flush: true)
            log.warn "Deleted blog: ${blog.title} - ${blog.id}"
            flash.message = "Deleted blog: ${blog.title} - ${blog.id}"
        } else {
            flash.message = "Blog with id ${params.id} was not found..."
        }
        redirect(controller: 'entries', action: 'recent')

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


    private User getCurrentUser() {
        springSecurityService.loadCurrentUser() as User
    }
}
