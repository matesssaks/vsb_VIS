package cz.vsb.and0153.campmanagement.person.controller;

import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import cz.vsb.and0153.campmanagement.person.service.PersonService;
import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import cz.vsb.and0153.campmanagement.server.controller.PostMapping;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.server.security.AuthUser;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

@Controller
public class PersonController extends AbstractController {

	private final PersonService service;

	public PersonController(TemplateEngine templateEngine, PersonService service) {
		super(templateEngine);
		this.service = service;
	}

	@GetMapping(path = "/persons", permissions = "logged")
	public void persons(HttpServletRequest request, HttpServletResponse response) throws IOException {
		WebContext context = getTemplateContext(request, response);
		context.setVariable("page", "persons");

		// Get persons of logged user
		AuthUser user = AuthUser.getUser(request);
		if (user instanceof UserDTO userDTO) {
			context.setVariable("persons", service.findAllByUser(userDTO));
		} else {
			context.setVariable("persons", Collections.<PersonDTO>emptyList());
		}

		render("user/persons", context);
	}

	private void renderEditModal(
		HttpServletRequest request,
		HttpServletResponse response,
		List<String> errors,
		PersonDTO person
	) throws IOException {
		WebContext context = getTemplateContext(request, response);
		context.setVariable("errors", errors);
		if (person == null) {
			context.setVariable("action", "/persons/new");
			context.setVariable("person", new PersonDTO());
		} else {
			context.setVariable("action", "/persons/edit/" + person.getId());
			context.setVariable("person", person);
		}
		render("user/modal/editPerson", context);
	}

	@GetMapping(path = "/persons/new", permissions = "logged")
	public void newPersonModal(HttpServletRequest request, HttpServletResponse response) throws IOException {
		renderEditModal(request, response, Collections.emptyList(), null);
	}

	@GetMapping(path = "/persons/edit/(\\d+)", permissions = "logged")
	public void editPersonModal(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException {
		renderEditModal(request, response, Collections.emptyList(), service.findById(Integer.parseInt(matcher.group(1))));
	}

	@PostMapping(path = "/persons/new", permissions = "logged")
	public void newPerson(HttpServletRequest request, HttpServletResponse response) throws IOException, UnitOfWorkException {
		try {
			PersonDTO person = new PersonDTO();
			person.setName(request.getParameter("name"));
			person.setTown(request.getParameter("town"));
			person.setStreet(request.getParameter("street"));
			person.setPostalCode(request.getParameter("postal"));
			person.setBornDate(LocalDate.parse(request.getParameter("born")).atStartOfDay().atOffset(ZoneOffset.UTC));

			if (AuthUser.getUser(request) instanceof UserDTO user) {
				service.save(person, user);
			}
		} catch (ListException e) {
			renderEditModal(request, response, e.getErrors(), null);
		}

		response.sendRedirect("/persons");
	}

	@PostMapping(path = "/persons/edit/(\\d+)", permissions = "logged")
	public void editPerson(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException, UnitOfWorkException {
		int id = Integer.parseInt(matcher.group(1));

		try {
			PersonDTO person = new PersonDTO();
			person.setId(id);
			person.setName(request.getParameter("name"));
			person.setTown(request.getParameter("town"));
			person.setStreet(request.getParameter("street"));
			person.setPostalCode(request.getParameter("postal"));
			person.setBornDate(LocalDate.parse(request.getParameter("born")).atStartOfDay().atOffset(ZoneOffset.UTC));

			if (AuthUser.getUser(request) instanceof UserDTO user) {
				service.save(person, user);
			}
		} catch (ListException e) {
			renderEditModal(request, response, e.getErrors(), service.findById(id));
		}

		response.sendRedirect("/persons");
	}

	@GetMapping(path = "/persons/delete/(\\d+)", permissions = "logged")
	public void deletePersonModal(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException {
		WebContext context = getTemplateContext(request, response);
		context.setVariable("person", service.findById(Integer.parseInt(matcher.group(1))));
		render("user/modal/deletePerson", context);
	}

	@PostMapping(path = "/persons/delete/(\\d+)", permissions = "logged")
	public void deletePerson(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException, UnitOfWorkException {
		service.delete(Integer.parseInt(matcher.group(1)));
		response.sendRedirect("/persons");
	}
}
