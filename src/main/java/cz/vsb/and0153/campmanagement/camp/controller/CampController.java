package cz.vsb.and0153.campmanagement.camp.controller;

import cz.vsb.and0153.campmanagement.camp.service.CampService;
import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.util.regex.Matcher;

@Controller
@Slf4j
public class CampController extends AbstractController {

	private final CampService campService;

	public CampController(TemplateEngine templateEngine, CampService campService) {
		super(templateEngine);
		this.campService = campService;
	}

	@GetMapping(path = "", permissions = "logged")
	public void camps(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		WebContext context = getTemplateContext(req, resp);
		context.setVariable("camps", campService.findAll());

		render("camps", context);
	}

	@GetMapping(path = "/camp/(\\d+)", permissions = "logged")
	public void camp(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
		WebContext context = getTemplateContext(req, resp);
		context.setVariable("camp", campService.findById(Integer.parseInt(matcher.group(1))));

		render("camp", context);
	}
}
