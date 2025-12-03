package cz.vsb.and0153.campmanagement.administration.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class AdministrationController {

	private final TemplateEngine engine;

	@GetMapping("/")
	public void index(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setStatus(HttpServletResponse.SC_OK);

		Context context = new Context(Locale.getDefault());
		context.setVariable("name", "Camp Management");

		String html = engine.process("administration", context);

		try (PrintWriter writer = resp.getWriter()) {
			writer.write(html);
		}
	}
}
