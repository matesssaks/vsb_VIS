package cz.vsb.and0153.campmanagement.user.controller;

import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

@Controller
public class LoginController extends AbstractController {

	public LoginController(TemplateEngine templateEngine) {
		super(templateEngine);
	}

	@GetMapping("/login")
	public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
		WebContext context = getTemplateContext(request, response);
		render("account/login", context);
	}

	@GetMapping("/signup")
	public void signup(HttpServletRequest request, HttpServletResponse response) throws IOException {
		WebContext context = getTemplateContext(request, response);
		render("account/signup", context);
	}
}
