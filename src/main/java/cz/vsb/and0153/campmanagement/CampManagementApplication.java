package cz.vsb.and0153.campmanagement;

import cz.vsb.and0153.campmanagement.server.WebServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
public class CampManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampManagementApplication.class, args);
	}

	@Bean
	CommandLineRunner startServer(Environment environment, WebServer server) {
		return args -> {
			log.info("Server starting with profiles: {}", String.join(", ", environment.getActiveProfiles()));

			// Start WebServer
			server.startAndWait();
		};
	}
}
