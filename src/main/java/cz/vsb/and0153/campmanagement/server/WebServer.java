package cz.vsb.and0153.campmanagement.server;

import cz.vsb.and0153.campmanagement.server.controller.ControllerServlet;
import cz.vsb.and0153.campmanagement.server.controller.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

@Slf4j
public class WebServer {

	private final Tomcat tomcat = new Tomcat();
	private final ControllerServlet servlet;

	public WebServer(String host, int port, String unregisteredPath) {
		this.servlet = new ControllerServlet(unregisteredPath);

		this.tomcat.setHostname(host);
		this.tomcat.setPort(port);
		this.tomcat.setBaseDir("tomcat");

		// Create Tomcat context
		String docBase = new File(".").getAbsolutePath();
		Context context = tomcat.addContext("", docBase);

		// Register main servlet
		Tomcat.addServlet(context, "main", servlet);
		context.addServletMappingDecoded("/*", "main");

		// Add MIME mappings
		Tomcat.addDefaultMimeTypeMappings(context);
	}

	public void startAndWait() throws LifecycleException {
		Host host = this.tomcat.getHost();
		Connector connector = this.tomcat.getConnector(); // This forces Tomcat to initialize

		this.tomcat.start();
		log.info("Server started on {}://{}:{}", connector.getScheme(), host.getName(), connector.getPort());
		this.tomcat.getServer().await();
	}

	public void registerRoute(Route route) {
		servlet.registerRoute(route);
	}
}
