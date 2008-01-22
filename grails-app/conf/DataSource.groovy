dataSource {
	pooled = false
	driverClassName = "org.postgresql.Driver"
	url = "jdbc:postgresql://localhost/groovyblogs"
	username = "glen"
	password = "password"
	dbCreate = "update"
}
// environment specific settings
/*
environments {
	development {
		dataSource {
			dbCreate = "create-drop" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:mem:devDB"
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:mem:testDb"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:prodDb;shutdown=true"
		}
	}
}
*/
