package uk.co.probablyfine.aoko;

import java.net.URL;
import java.security.ProtectionDomain;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class AokoBootstrap {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		ProtectionDomain domain = AokoBootstrap.class.getProtectionDomain();
		URL location = domain.getCodeSource().getLocation();
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
		webapp.setServer(server);
		webapp.setWar(location.toExternalForm());
		server.setHandler(webapp);
		server.start();
		server.join();
	}
}
