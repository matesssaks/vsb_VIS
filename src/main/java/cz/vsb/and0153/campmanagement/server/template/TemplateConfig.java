package cz.vsb.and0153.campmanagement.server.template;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateConfig {

	/**
	 * Create a template resolver
	 */
	@Bean
	public ClassLoaderTemplateResolver templateResolver(@Value("${template.cache}") Boolean enableCache) {
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setCharacterEncoding("UTF-8");
		resolver.setCacheable(enableCache);
		return resolver;
	}

	/**
	 * Create a message source
	 */
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasenames("messages/messages");
		source.setDefaultEncoding("UTF-8");
		return source;
	}

	/**
	 * Create a layout dialect
	 */
	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}

	/**
	 * Create a template engine
	 */
	@Bean
	public SpringTemplateEngine templateEngine(
		ClassLoaderTemplateResolver resolver,
		MessageSource messageSource,
		LayoutDialect layoutDialect
	) {
		// Initialize template engine
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(resolver); // Set the template resolver
		engine.setMessageSource(messageSource); // Set the message source
		engine.addDialect(layoutDialect); // Add the layout dialect
		return engine;
	}
}
