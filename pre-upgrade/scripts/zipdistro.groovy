/*
   Small Gant script to zip up a distro of groovyblogs.

   You can run "grails zipdistro" to generate the zip.
*/

target(zipdistro: "Zips an application distro for upload to googlecode etc") {

    def baseDir = ant.project.properties.basedir

    ant.property ( file : 'application.properties' )
    def appName = ant.project.properties.'app.name'
    def appVersion = ant.project.properties.'app.version'

    println "Starting Zip Disto creation for ${appName} version ${appVersion} at ${new Date()}"
    def zipDir = "distro"
    def zipName = "${appName}-${appVersion}"
    def buildDir = "${zipDir}/${zipName}"
    def tarName = "${buildDir}.tar"
    def tgzName = "${tarName}.gz"

    // println "${ant.project.properties}"

    delete(dir: buildDir)
    mkdir(dir: buildDir)

    copy ( todir : buildDir ) {
        fileset ( dir : baseDir ) {
            include ( name: "src/**")
            include ( name: "test/**")
            include ( name: "lib/**")
            include ( name: "art/**")
            include ( name: "spring/**")
            include ( name: "hibernate/**")
            include ( name: "scripts/**")
            include ( name: "plugins/**")
            include ( name: "grails-app/**")
            include ( name: "web-app/**")
            exclude ( name: "plugins/*.zip")
            exclude ( name: "**/WEB-INF/lib/**")
            exclude ( name: "**/WEB-INF/plugins/**")
            exclude ( name: "**/web-app/plugins/**")

        }
        fileset ( dir : baseDir, excludes: "*.war,*.zip,*.log,TEST*.xml,devDB.*")
    }

   tar( destfile: tarName, basedir: zipDir, includes: "${zipName}/**" )
   gzip( src: tarName, destfile: tgzName )
   delete ( file: tarName )
   delete( dir: buildDir )
}

setDefaultTarget 'zipdistro'
