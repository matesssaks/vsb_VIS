package cz.vsb.and0153.campmanagement;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

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
	CommandLineRunner startServer() {
		return args -> {
			// Create a Tomcat server
			Tomcat tomcat = new Tomcat();
			tomcat.setPort(serverPort);
			tomcat.setHostname(serverAddress);
			tomcat.setBaseDir("tomcat");

			// Set up Tomcat
			String base = new File(".").getAbsolutePath();
			Context ctx = tomcat.addContext("", base);

			//String servletName = "registraceOsobyServlet";
			//Tomcat.addServlet(ctx, servletName, new RegistraceOsobyServlet(registraceOsobyService));
			//ctx.addServletMappingDecoded("/registrace-osoby", servletName);

			// Start Tomcat
			tomcat.start();
			tomcat.getServer().await();
		};
	}
}
