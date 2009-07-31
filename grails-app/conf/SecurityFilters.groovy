import org.codehaus.groovy.grails.commons.ApplicationHolder

class SecurityFilters {
    
    def filters = {

       accountOperations(controller: "account", action: "(index|edit|update|deleteFeed|addFeed|testFeed)") {
            before = {
                accessControl(auth: false) {
                    role("user") | role("admin")
                }
            }
        }

        adminOperations(controller: "account", action: "(approveFeed|removeFeed)") {
            before = {
                accessControl(auth: false) {
                    role("admin")
                }
            }
        }

        adminScaffold(controller: "blog", action: "*") {
            before = {
                accessControl(auth: false) {
                    role("admin")
                }
            }
        }

        loggingStuff(controller: "runtimeLogging", action: "*") {
            before = {
                accessControl(auth: false) {
                    role("admin")
                }
            }
        }
       
    }
}
