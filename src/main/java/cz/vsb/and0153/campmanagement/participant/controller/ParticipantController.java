package cz.vsb.and0153.campmanagement.participant.controller;

import cz.vsb.and0153.campmanagement.camp.service.CampService;
import cz.vsb.and0153.campmanagement.participant.ParticipantService;
import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
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
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

@Controller
public class ParticipantController extends AbstractController {

	private final ParticipantService participantService;
	private final CampService campService;
	private final PersonService personService;

	public ParticipantController(
		TemplateEngine templateEngine,
		ParticipantService participantService,
		CampService campService,
		PersonService personService
	) {
		super(templateEngine);
		this.participantService = participantService;
		this.campService = campService;
		this.personService = personService;
	}

	@GetMapping(path = "/registrations", permissions = "logged")
	public void registrations(HttpServletRequest request, HttpServletResponse response) throws IOException {
		WebContext context = getTemplateContext(request, response);
		context.setVariable("page", "registrations");

		// Get participants of logged user
		AuthUser user = AuthUser.getUser(request);
		if (user instanceof UserDTO userDTO) {
			context.setVariable("participants", participantService.findAllByUser(userDTO));
		} else {
			context.setVariable("participants", Collections.<ParticipantDTO>emptyList());
		}

		render("user/registrations", context);
	}

	private void renderRegisterModal(
		HttpServletRequest request,
		HttpServletResponse response,
		List<String> errors,
		PersonDTO person
	) throws IOException {
		WebContext context = getTemplateContext(request, response);
		context.setVariable("errors", errors);
		context.setVariable("camps", campService.findAll());
		context.setVariable("person", Objects.requireNonNullElseGet(person, PersonDTO::new));
		render("user/modal/registerPerson", context);
	}

	@GetMapping(path = "/persons/register/(\\d+)", permissions = "logged")
	public void editPersonModal(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException {
		renderRegisterModal(request, response, Collections.emptyList(), personService.findById(Integer.parseInt(matcher.group(1))));
	}

	@PostMapping(path = "/persons/register", permissions = "logged")
	public void newPerson(HttpServletRequest request, HttpServletResponse response) throws IOException, UnitOfWorkException {
		try {
			// Find existing person or create new one
			PersonDTO person = personService.findPersonalNumber(request.getParameter("personalNumber"));
			if (person == null) person = new PersonDTO();

			// Update person data
			person.setName(request.getParameter("name"));
			person.setPersonalNumber(request.getParameter("personalNumber"));
			person.setTown(request.getParameter("town"));
			person.setStreet(request.getParameter("street"));
			person.setPostalCode(request.getParameter("postal"));
			person.setBornDate(LocalDate.parse(request.getParameter("born")).atStartOfDay().atOffset(ZoneOffset.UTC));

			// Save person
			if (AuthUser.getUser(request) instanceof UserDTO user) {
				personService.save(person, user);
			}

			// Register participant
			participantService.register(person, campService.findById(Integer.parseInt(request.getParameter("camp"))));
		} catch (ListException e) {
			renderRegisterModal(request, response, e.getErrors(), null);
		}

		response.sendRedirect("/registrations");
	}
}
