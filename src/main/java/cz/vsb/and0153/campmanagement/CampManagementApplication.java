package cz.vsb.and0153.campmanagement;

import cz.vsb.and0153.campmanagement.server.WebServer;
import cz.vsb.and0153.campmanagement.server.statics.StaticFilesServlet;
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
import org.springframework.util.StringUtils;

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

			WebServer server = new WebServer(host, port);

			// Register controllers
			server.registerControllers(this.context.getBeansWithAnnotation(Controller.class).values());

			// Register static files
			Resource staticFiles = this.context.getResource(
				staticFilesPath.endsWith("/") ? staticFilesPath : staticFilesPath + "/"
			);
			server.registerServlet("/static/*", new StaticFilesServlet(staticFiles));

			// Start WebServer
			server.startAndWait();
		};
	}
}
