package cz.vsb.and0153.campmanagement.user.controller;

import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import cz.vsb.and0153.campmanagement.server.controller.PostMapping;
import cz.vsb.and0153.campmanagement.server.security.AuthUser;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.user.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
public class LoginController extends AbstractController {

	private final LoginService service;

	public LoginController(TemplateEngine templateEngine, LoginService service) {
		super(templateEngine);
		this.service = service;
	}

	private void renderPage(
		String template,
		HttpServletRequest req,
		HttpServletResponse resp,
		List<String> errors
	) throws IOException {
		WebContext context = getTemplateContext(req, resp);
		context.setVariable("errors", errors);
		render(template, context);
	}

	@GetMapping(path = "/login", permissions = "*")
	public void loginPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		renderPage("account/login", req, resp, Collections.emptyList());
	}

	@GetMapping(path = "/signup", permissions = "*")
	public void signupPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		renderPage("account/signup", req, resp, Collections.emptyList());
	}

	@PostMapping(path = "/login", permissions = "*")
	public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException, GeneralSecurityException {
		String email = req.getParameter("email");
		String password = req.getParameter("password");

		try {
			AuthUser.setUser(req, service.login(email, password));
		} catch (ListException e) {
			renderPage("account/login", req, resp, e.getErrors());
			return;
		}

		resp.sendRedirect("/");
	}

	@PostMapping(path = "/signup", permissions = "*")
	public void signup(HttpServletRequest req, HttpServletResponse resp) throws IOException, GeneralSecurityException, UnitOfWorkException {
		String email = req.getParameter("email");
		String firstName = req.getParameter("firstname");
		String lastName = req.getParameter("lastname");
		String password = req.getParameter("password");
		String password2 = req.getParameter("password2");

		try {
			AuthUser.setUser(req, service.register(email, firstName, lastName, password, password2));
		} catch (ListException e) {
			renderPage("account/signup", req, resp, e.getErrors());
			return;
		}

		resp.sendRedirect("/");
	}

	@PostMapping(path = "/logout", permissions = "*")
	public void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		AuthUser.setUser(req, null);
		resp.sendRedirect("/");
	}
}
