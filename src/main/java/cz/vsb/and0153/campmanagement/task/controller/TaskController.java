package cz.vsb.and0153.campmanagement.task.controller;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.service.CampService;
import cz.vsb.and0153.campmanagement.participant.ParticipantService;
import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
import cz.vsb.and0153.campmanagement.server.controller.AbstractController;
import cz.vsb.and0153.campmanagement.server.controller.GetMapping;
import cz.vsb.and0153.campmanagement.server.controller.PostMapping;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.task.dto.TaskDTO;
import cz.vsb.and0153.campmanagement.task.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
@Controller
public class TaskController extends AbstractController {

	private final TaskService taskService;
	private final CampService campService;
	private final ParticipantService participantService;

	public TaskController(
		TemplateEngine templateEngine,
		TaskService taskService,
		CampService campService,
		ParticipantService participantService
	) {
		super(templateEngine);
		this.taskService = taskService;
		this.campService = campService;
		this.participantService = participantService;
	}

	@GetMapping(path = "/camp/(\\d+)/tasks", permissions = "logged")
	public void tasks(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		WebContext context = getTemplateContext(req, resp);
		context.setVariable("page", "tasks");
		context.setVariable("camp", camp);
		context.setVariable("tasks", taskService.findAllTasksByCamp(camp));
		render("dashboard/tasks", context);
	}

	@GetMapping(path = "/camp/(\\d+)/medic", permissions = "logged")
	public void medic(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		WebContext context = getTemplateContext(req, resp);
		context.setVariable("page", "medic");
		context.setVariable("camp", camp);
		context.setVariable("tasks", Collections.<TaskDTO>emptyList());
		render("dashboard/medic", context);
	}

	@PostMapping(path = "/camp/(\\d+)/medic", permissions = "logged")
	public void generateDrugPlan(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		WebContext context = getTemplateContext(req, resp);
		context.setVariable("page", "medic");
		context.setVariable("camp", camp);
		context.setVariable("tasks", taskService.generateDrugPlan(camp));
		render("dashboard/medic", context);
	}

	private void renderEditModal(
		HttpServletRequest request,
		HttpServletResponse response,
		List<String> errors,
		TaskDTO task,
		CampDTO camp
	) throws IOException {
		WebContext context = getTemplateContext(request, response);
		context.setVariable("errors", errors);
		if (task == null) {
			context.setVariable("action", "/camp/" + camp.getId() + "/task/new");
			context.setVariable("task", TaskDTO.builder().build());
		} else {
			context.setVariable("action", "/camp/" + camp.getId() + "/task/edit/" + task.getId());
			context.setVariable("task", task);
		}
		context.setVariable("participants", participantService.findAllByCamp(camp));
		render("dashboard/modal/editTask", context);
	}

	@GetMapping(path = "/camp/(\\d+)/task/new", permissions = "logged")
	public void newTaskModal(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		renderEditModal(request, response, Collections.emptyList(), null, camp);
	}

	@GetMapping(path = "/camp/(\\d+)/task/edit/(\\d+)", permissions = "logged")
	public void editTaskModal(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		renderEditModal(
			request, response,
			Collections.emptyList(),
			taskService.findById(Integer.parseInt(matcher.group(2))),
			camp
		);
	}

	@PostMapping(path = "/camp/(\\d+)/task/new", permissions = "logged")
	public void newTask(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException, UnitOfWorkException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		try {
			TaskDTO.TaskDTOBuilder builder = TaskDTO.builder()
				.shortDescription(request.getParameter("short"))
				.description(request.getParameter("description"))
				.startDate(LocalDate.parse(request.getParameter("start")).atStartOfDay().atOffset(ZoneOffset.UTC))
				.dueDate(LocalDate.parse(request.getParameter("due")).atStartOfDay().atOffset(ZoneOffset.UTC))
				.createdAt(OffsetDateTime.now());

			List<ParticipantDTO> participants = Arrays.stream(request.getParameterValues("assignee"))
				.map(val -> participantService.findById(Integer.parseInt(val))).toList();

			builder.participants(participants);

			taskService.save(builder.build());
		} catch (ListException e) {
			renderEditModal(request, response, e.getErrors(), null, camp);
		}

		response.sendRedirect("/camp/" + camp.getId() + "/tasks");
	}

	@PostMapping(path = "/camp/(\\d+)/task/edit/(\\d+)", permissions = "logged")
	public void editTask(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException, UnitOfWorkException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		int id = Integer.parseInt(matcher.group(2));

		try {
			TaskDTO task = TaskDTO.builder().build();
			task.setId(id);
			task.setShortDescription(request.getParameter("short"));
			task.setDescription(request.getParameter("description"));
			task.setStartDate(LocalDate.parse(request.getParameter("start")).atStartOfDay().atOffset(ZoneOffset.UTC));
			task.setDueDate(LocalDate.parse(request.getParameter("due")).atStartOfDay().atOffset(ZoneOffset.UTC));
			task.setCreatedAt(OffsetDateTime.now());

			List<ParticipantDTO> participants = Arrays.stream(request.getParameterValues("assignee"))
				.map(val -> participantService.findById(Integer.parseInt(val))).toList();

			task.setParticipants(participants);

			taskService.save(task);
		} catch (ListException e) {
			renderEditModal(request, response, e.getErrors(), taskService.findById(id), camp);
		}

		response.sendRedirect("/camp/" + camp.getId() + "/tasks");
	}
}
