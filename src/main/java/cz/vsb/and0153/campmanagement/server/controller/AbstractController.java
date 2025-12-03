package cz.vsb.and0153.campmanagement.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@RequiredArgsConstructor
public abstract class AbstractController {

	private final TemplateEngine templateEngine;

	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	public Locale getLocale() {
		return DEFAULT_LOCALE;
	}

	public Context getTemplateContext() {
		return new Context(getLocale());
	}

	public void render(String template, Context context, HttpServletResponse resp) throws IOException {
		// Render template
		String html = templateEngine.process(template, context);

		// Send response
		resp.setContentType("text/html;charset=UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);
		@Cleanup PrintWriter writer = resp.getWriter();
		writer.write(html);
	}
}
