import org.codehaus.groovy.grails.commons.ConfigurationHolder

class MailService {
		
	boolean transactional = false
	
	def send(String toaddress, String content, String msgSubject) {
		if (ConfigurationHolder.config.mail.enabled) {
			new AntBuilder().mail(
				mailhost: ConfigurationHolder.config.mail.host, 
				subject: msgSubject) { 
			    	from(address:   ConfigurationHolder.config.mail.from) 
			    	to  (address: toaddress) 
			    	message(mimetype: "text/html", wrapWithHtml(content) ) 
			}
		} else {
			log.warn("Mail integration disabled\nTo: $toaddress\n$content");
		}
	}
	
	def wrapWithHtml(msg) {
		def wrapped = """
		<html>
    	<head>
    		<style>
    		body {
    			color: #4D4D4D;
    			font-family: Tahoma, Geneva, Arial, Helvetica, sans-serif;
    			font-size: 0.8em;
    			line-height: 1.8em;
    			text-align: left;
    			background-off: #E6E6E6;
    			}

    			a {
    			color: #005880;
    			}

    			a:hover {
    			color: #006B95;
    			}

    			a:visited {
    			color: #006B95;
    			}

    			a:visited:hover {
    			color: #2C91B2;
    			}

    			h1{
    			font-family: "Arial Narrow",Tahoma, Geneva, Arial, Helvetica, sans-serif;
    			font-size: 1.6em;
    			color: #006B95;
    			margin: 15px 0 50px 0;
    			padding-left: 15px; 
    			}

    			p {
    			margin: 10px 15px 5px 15px;
    			}
    		
    		</style>
    	</head>
    	<body>
    	${msg}
		</body>
		</html>
		"""
		return wrapped
	}
}

