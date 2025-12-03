package cz.vsb.and0153.campmanagement;

import cz.vsb.and0153.campmanagement.server.controller.ControllerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.io.File;

@Slf4j
@SpringBootApplication
public class CampManagementApplication {

	@Value("${server.port}")
	private int serverPort;

	@Value("${server.address}")
	private String serverAddress;

	public static void main(String[] args) {
		SpringApplication.run(CampManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner startServer(Environment environment, ControllerRegistry controllerRegistry) {
		return args -> {
			log.info("Server starting with profiles: {}", String.join(", ", environment.getActiveProfiles()));

			// Create a Tomcat server
			Tomcat tomcat = new Tomcat();
			tomcat.setPort(serverPort);
			tomcat.setHostname(serverAddress);
			tomcat.setBaseDir("tomcat");
			tomcat.getConnector(); // Force Tomcat to initialize

			// Set up Tomcat
			String docBase = new File(".").getAbsolutePath();
			Context ctx = tomcat.addContext("", docBase);

			// Register controllers
			controllerRegistry.registerControllers(ctx);

			// Start Tomcat
			tomcat.start();
			log.info("Server started on http://{}:{}", serverAddress, serverPort);
			tomcat.getServer().await();
		};
	}
}
