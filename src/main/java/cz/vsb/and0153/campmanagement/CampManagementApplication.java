package cz.vsb.and0153.campmanagement;

import cz.vsb.and0153.campmanagement.server.WebServer;
import cz.vsb.and0153.campmanagement.server.controller.Route;
import cz.vsb.and0153.campmanagement.server.statics.StaticFilesController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class CampManagementApplication {

	private final ApplicationContext context;

	public static void main(String[] args) {
		SpringApplication.run(CampManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner startServer(
		Environment environment,
		@Value("${server.host}") String host,
		@Value("${server.port}") int port,
		@Value("${server.static}") String staticFilesPath
	) {
		return args -> {
			log.info("Server starting with profiles: {}", String.join(", ", environment.getActiveProfiles()));

			WebServer server = new WebServer(host, port, "/login");

			// Register controllers
			this.context.getBeansWithAnnotation(Controller.class).values().forEach(controller -> {
				for (Method method : controller.getClass().getMethods()) {
					// Register route in servlet
					Arrays.stream(Route.RouteType.values())
						.map(type -> type.getRoute(controller, method))
						.filter(Objects::nonNull)
						.forEach(server::registerRoute);
				}
			});

			Resource staticFilesDir = this.context.getResource(
				staticFilesPath.endsWith("/") ? staticFilesPath : staticFilesPath + "/"
			);
			server.registerRoute(StaticFilesController.getRoute("/static/(.+)", staticFilesDir));

			// Start WebServer
			server.startAndWait();
		};
	}
}
