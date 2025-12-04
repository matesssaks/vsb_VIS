package cz.vsb.and0153.campmanagement.administration.controller;

import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

@Slf4j
@Controller
public class AdministrationController extends AbstractController {

	public AdministrationController(TemplateEngine templateEngine) {
		super(templateEngine);
	}

	@GetMapping("/administration")
	public void index(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		WebContext context = getTemplateContext(req, resp);
		context.setVariable("name", "Camp Management");

		render("administration", context, resp);
	}
}
