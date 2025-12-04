package cz.vsb.and0153.campmanagement.administration.controller;

import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

@Controller
public class IndexController extends AbstractController {

	public IndexController(TemplateEngine templateEngine) {
		super(templateEngine);
	}

	@GetMapping("")
	public void index(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		WebContext context = getTemplateContext(req, resp);
		render("index", context, resp);
	}
}
