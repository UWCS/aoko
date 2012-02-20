Aoko, a music server written in as close to pure java as I can get.

License: MIT

Requirements:

 * Maven (have tested with Maven3)
 * Java

How to run

 * Modify the musicserver.properties.template appropriately in /src/main/resources and rename to musicserver.properties
 * Set up correct database config in app-database.xml (in /src/main/resources/spring)
 * By default is set to run using derby, but can be run in MySQL by commenting in/out and modifying the appropriate parts of app-database and pom.xml (for the connector dependency)
 * To run, mvn clean jetty:run.
	
TODO:

 * Make sure everything works end-to-end
 * A UI that doesn't want make me want to die inside
 * Search
 * Scrobble
 * IRC notices.