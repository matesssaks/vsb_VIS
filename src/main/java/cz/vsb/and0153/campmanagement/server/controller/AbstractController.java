package cz.vsb.and0153.campmanagement.server.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@RequiredArgsConstructor
public abstract class AbstractController {

	private final TemplateEngine templateEngine;
	private static JakartaServletWebApplication webApplication;

	private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

	public Locale getLocale() {
		return DEFAULT_LOCALE;
	}

	private static void setWebApplication(ServletContext servletContext) {
		webApplication = JakartaServletWebApplication.buildApplication(servletContext);
	}

	public WebContext getTemplateContext(HttpServletRequest req, HttpServletResponse resp) {
		if (
			webApplication == null ||
			webApplication.getNativeServletContextObject() != req.getServletContext()
		) {
			setWebApplication(req.getServletContext());
		}

		return new WebContext(webApplication.buildExchange(req, resp), getLocale());
	}

	public void render(String template, WebContext context) throws IOException {
		HttpServletResponse resp = (HttpServletResponse) ((IServletWebExchange) context.getExchange()).getNativeResponseObject();
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html");

		// Render template
		PrintWriter writer = resp.getWriter();
		templateEngine.process(template, context, writer);
		writer.close();
	}
}
