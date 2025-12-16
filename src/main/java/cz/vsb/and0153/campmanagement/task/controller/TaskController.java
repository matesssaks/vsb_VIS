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
import java.time.LocalDateTime;
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
				.startDate(LocalDateTime.parse(request.getParameter("start")).atOffset(ZoneOffset.UTC))
				.dueDate(LocalDateTime.parse(request.getParameter("due")).atOffset(ZoneOffset.UTC))
				.createdAt(OffsetDateTime.now());

			List<ParticipantDTO> participants = Arrays.stream(request.getParameterValues("assignee"))
				.map(val -> participantService.findById(Integer.parseInt(val))).toList();

			builder.participants(participants);

			taskService.save(builder.build());
		} catch (ListException e) {
			renderEditModal(request, response, e.getErrors(), null, camp);
			return;
		}

		response.sendRedirect("/camp/" + camp.getId() + "/tasks");
	}

	@PostMapping(path = "/camp/(\\d+)/task/edit/(\\d+)", permissions = "logged")
	public void editTask(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws IOException, UnitOfWorkException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));
		TaskDTO task = taskService.findById(Integer.parseInt(matcher.group(2)));

		try {
			task.setShortDescription(request.getParameter("short"));
			task.setDescription(request.getParameter("description"));
			task.setStartDate(LocalDateTime.parse(request.getParameter("start")).atOffset(ZoneOffset.UTC));
			task.setDueDate(LocalDateTime.parse(request.getParameter("due")).atOffset(ZoneOffset.UTC));

			List<ParticipantDTO> participants = Arrays.stream(request.getParameterValues("assignee"))
				.map(val -> participantService.findById(Integer.parseInt(val))).toList();

			task.setParticipants(participants);

			taskService.save(task);
		} catch (ListException e) {
			renderEditModal(request, response, e.getErrors(), task, camp);
			return;
		}

		response.sendRedirect("/camp/" + camp.getId() + "/tasks");
	}

	@GetMapping(path = "/camp/(\\d+)/task/toggle/(\\d+)", permissions = "logged")
	public void toggleTask(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws UnitOfWorkException, ListException, IOException {
		int campId = Integer.parseInt(matcher.group(1));
		TaskDTO task = taskService.findById(Integer.parseInt(matcher.group(2)));

		// Toggle state
		if (task.getFinishAt() == null) {
			task.setFinishAt(OffsetDateTime.now());
		} else {
			task.setFinishAt(null);
		}

		taskService.save(task);

		response.sendRedirect("/camp/" + campId + "/tasks");
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
	public void generateDrugPlan(HttpServletRequest req, HttpServletResponse resp, Matcher matcher) throws IOException, UnitOfWorkException {
		CampDTO camp = campService.findById(Integer.parseInt(matcher.group(1)));

		// Web template context
		WebContext context = getTemplateContext(req, resp);
		context.setVariable("page", "medic");
		context.setVariable("camp", camp);
		List<ParticipantDTO> medics = participantService.findAllByCamp(camp).stream()
			.filter(p -> p.getRole() != null && Boolean.TRUE.equals(p.getRole().getCanViewDrugs()))
			.toList();
		context.setVariable("participants", medics);

		// If we're saving instead of generating
		boolean hasMedicParams = req.getParameterMap().keySet().stream().anyMatch(k -> k != null && k.startsWith("medic[") && k.endsWith("]"));
		if (hasMedicParams) {
			try {
				for (String key : req.getParameterMap().keySet()) {
					if (key == null || !key.startsWith("medic[") || !key.endsWith("]")) continue; // Take medic[] as key
					String idStr = key.substring("medic[".length(), key.length() - 1);

					String medicIdStr = req.getParameter(key);
					String shortDesc = req.getParameter("shortDescription[" + idStr + "]");
					String description = req.getParameter("description[" + idStr + "]");
					String startStr = req.getParameter("startDate[" + idStr + "]");
					String dueStr = req.getParameter("dueDate[" + idStr + "]");

					ParticipantDTO medic = participantService.findById(Integer.parseInt(medicIdStr));

					TaskDTO task = TaskDTO.builder()
						.shortDescription(shortDesc)
						.description(description)
						.startDate(LocalDateTime.parse(startStr).atOffset(ZoneOffset.UTC))
						.dueDate(LocalDateTime.parse(dueStr).atOffset(ZoneOffset.UTC))
						.participants(List.of(medic))
						.build();

					taskService.save(task);
				}

				resp.sendRedirect("/camp/" + camp.getId() + "/tasks");
				return;
			} catch (ListException e) {
				context.setVariable("tasks", Collections.<TaskDTO>emptyList());
				context.setVariable("errors", e.getErrors());
				render("dashboard/medic", context);
				return;
			}
		}

		try {
			context.setVariable("tasks", taskService.generateDrugPlan(camp));
		} catch (ListException e) {
			context.setVariable("tasks", Collections.<TaskDTO>emptyList());
			context.setVariable("errors", e.getErrors());
		}
		render("dashboard/medic", context);
	}

	@GetMapping(path = "/camp/(\\d+)/task/delete/(\\d+)", permissions = "logged")
	public void deleteTask(HttpServletRequest request, HttpServletResponse response, Matcher matcher) throws UnitOfWorkException, IOException {
		taskService.delete(Integer.parseInt(matcher.group(2)));
		response.sendRedirect("/camp/" + Integer.parseInt(matcher.group(1)) + "/tasks");
	}
}
