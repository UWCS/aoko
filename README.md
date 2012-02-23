Aoko, a music server written in as close to pure java as I can get.

License: MIT

Requirements:

 * Maven (works with 2 and 3)
 * Java
 * MySQL (if using the MySQL database config)

How to run

 * Modify the musicserver.properties.template appropriately in /src/main/resources and rename to musicserver.properties
 * Set up correct database config in app-database.xml (in /src/main/resources/spring)
 * Select plugins for use in app-plugins.xml (in /src/main/resources/spring)
 * By default is set to run using derby, but can be run in MySQL by commenting in/out and modifying the appropriate parts of app-database and pom.xml (for the connector dependency)
 * To run, mvn clean jetty:run.
	
TODO:

 * A UI that doesn't want make me want to die inside
 * Search
 * Scrobble
 * IRC notices.