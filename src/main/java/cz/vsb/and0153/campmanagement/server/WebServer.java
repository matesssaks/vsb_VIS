package cz.vsb.and0153.campmanagement.server;

import cz.vsb.and0153.campmanagement.server.controller.ControllerServlet;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import cz.vsb.and0153.campmanagement.server.controller.MappingMethod;
import cz.vsb.and0153.campmanagement.server.controller.PostMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebServer {

	private final ApplicationContext applicationContext;
	private final Tomcat tomcat;

	public WebServer(
		@Value("${server.host}") String host,
		@Value("${server.port}") int port,
		ApplicationContext applicationContext
	) {
		this.applicationContext = applicationContext;

		this.tomcat = new Tomcat();
		this.tomcat.setHostname(host);
		this.tomcat.setPort(port);
		this.tomcat.setBaseDir("tomcat");

		// Set up Tomcat
		String docBase = new File(".").getAbsolutePath();
		Context ctx = tomcat.addContext("", docBase);

		// Register controllers
		registerControllers(ctx);
	}

	public void startAndWait() throws LifecycleException {
		Host host = this.tomcat.getHost();
		Connector connector = this.tomcat.getConnector(); // Force Tomcat to initialize

		this.tomcat.start();
		log.info("Server started on {}://{}:{}", connector.getScheme(), host.getName(), connector.getPort());
		this.tomcat.getServer().await();
	}

	private void registerControllers(Context context) {
		Collection<Object> controllers = this.applicationContext.getBeansWithAnnotation(Controller.class).values();

		Map<String, ControllerServlet.ControllerServletBuilder> servlets = new HashMap<>();

		// Build servlets for each path
		controllers.forEach(controller -> {
			Class<?> controllerClass = controller.getClass();

			for (Method method : controllerClass.getMethods()) {
				// GET method handler
				GetMapping getMapping = method.getAnnotation(GetMapping.class);
				if (getMapping != null) {
					servlets.putIfAbsent(getMapping.value(), ControllerServlet.builder());
					servlets.get(getMapping.value()).getMethod(new MappingMethod(controller, method));

					continue;
				}

				// POST method handler
				PostMapping postMapping = method.getAnnotation(PostMapping.class);
				if (postMapping != null) {
					servlets.putIfAbsent(postMapping.value(), ControllerServlet.builder());
					servlets.get(postMapping.value()).postMethod(new MappingMethod(controller, method));
				}
			}
		});

		// Register servlets
		servlets.forEach((path, builder) -> {
			ControllerServlet servlet = builder.build();

			log.debug("Registering servlet '{}' for path '{}'", servlet, path);

			Tomcat.addServlet(context, servlet.toString(), servlet);
			context.addServletMappingDecoded(path, servlet.toString());
		});
	}
}
