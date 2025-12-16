package cz.vsb.and0153.campmanagement.task.service;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.participant.ParticipantService;
import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
import cz.vsb.and0153.campmanagement.person.dto.DrugDTO;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.task.dto.TaskDTO;
import cz.vsb.and0153.campmanagement.task.mapper.TaskMapper;
import cz.vsb.and0153.campmanagement.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository repository;

	private final TaskMapper taskMapper;
	private final CampMapper campMapper;

	private final ParticipantService participantService;

	public TaskDTO findById(int id) {
		return taskMapper.toDTO(repository.findById(id));
	}

	public List<TaskDTO> findAllTasksByCamp(CampDTO camp) {
		return repository.findAllByCampId(campMapper.toEntity(camp)).stream().map(taskMapper::toDTO).toList();
	}

	public List<TaskDTO> generateDrugPlan(CampDTO camp) throws ListException {
		List<ParticipantDTO> participants = participantService.findAllByCamp(camp);

		List<ParticipantDTO> medics = participants.stream()
			.filter(p -> p.getRole() != null && Boolean.TRUE.equals(p.getRole().getCanViewDrugs()))
			.toList();

		// Get start and end date of the camp
		OffsetDateTime start = camp.getStartDate();
		OffsetDateTime end = camp.getEndDate();
		if (start == null || end == null || !end.isAfter(start)) {
			return List.of(); // No start or end date
		}

		List<TaskDTO> tasks = new ArrayList<>();
		int taskIdx = 0;
		for (ParticipantDTO p : participants) {
			if (p.getPerson() == null || p.getPerson().getDrugs() == null) continue;
			for (DrugDTO drug : p.getPerson().getDrugs()) {
				// Validate drug
				if (drug == null) continue;
				Integer dosageHours = drug.getDosage();
				if (dosageHours == null || dosageHours <= 0) continue;

				// Create the task
				OffsetDateTime t = start.plusHours(dosageHours);
				while (!t.isAfter(end)) {
					OffsetDateTime due = t.plusMinutes(15);
					TaskDTO task = TaskDTO.builder()
						.id(taskIdx++)
						.shortDescription("Give drug: " + Objects.toString(drug.getName(), "") + " to " + (p.getPerson().getName() != null ? p.getPerson().getName() : ("Participant #" + p.getId())))
						.description(Objects.toString(drug.getDescription(), ""))
						.startDate(t)
						.dueDate(due)
						.build();
					tasks.add(task);
					// Next taking time by dosage
					t = t.plusHours(dosageHours);
				}
			}
		}

		// Sort tasks by start time
		tasks.sort(Comparator.comparing(TaskDTO::getStartDate));

		record Interval(OffsetDateTime start, OffsetDateTime end) {}

		// Build intervals
		Map<Integer, List<Interval>> medicBusy = new HashMap<>();
		List<TaskDTO> existingTasks = findAllTasksByCamp(camp);
		for (TaskDTO t : existingTasks) {
			if (t.getParticipants() == null || t.getStartDate() == null || t.getDueDate() == null) continue;
			for (ParticipantDTO participant : t.getParticipants()) {
				// Consider only medics
				if (participant.getRole() != null && Boolean.TRUE.equals(participant.getRole().getCanViewDrugs())) {
					medicBusy.computeIfAbsent(participant.getId(), k -> new ArrayList<>())
						.add(new Interval(t.getStartDate(), t.getDueDate()));
				}
			}
		}

		// Assign generated drug tasks to medics without overlapping existing tasks
		for (TaskDTO task : tasks) {
			ParticipantDTO assigned = null;
			for (ParticipantDTO medic : medics) {
				List<Interval> intervals = medicBusy.computeIfAbsent(medic.getId(), k -> new ArrayList<>());
				boolean overlaps = false;
				for (Interval i : intervals) {
					if (task.getStartDate() != null && task.getDueDate() != null
						&& task.getStartDate().isBefore(i.end())
						&& task.getDueDate().isAfter(i.start())
					) {
						overlaps = true;
						break;
					}
				}
				if (!overlaps) {
					assigned = medic;
					// Reserve interval for this medic
					intervals.add(new Interval(task.getStartDate(), task.getDueDate()));
					break;
				}
			}

			if (assigned == null) {
				throw new ListException(List.of("No possible solution for drug plan can be generated."));
			}

			task.setParticipants(List.of(assigned));
		}

		return tasks;
	}

	public TaskDTO save(TaskDTO task) throws ListException, UnitOfWorkException {
		List<String> errors = new ArrayList<>();

		// Validate if all fields are filled
		if (task.getShortDescription() == null || task.getShortDescription().isBlank()) errors.add("Short description is required.");
		if (task.getDescription() == null || task.getDescription().isBlank()) errors.add("Description is required.");
		if (task.getStartDate() == null) errors.add("Start date is required.");
		if (task.getDueDate() == null) errors.add("Due date is required.");
		if (task.getParticipants() == null || task.getParticipants().isEmpty()) errors.add("Participants are required.");
		if (task.getStartDate() != null && task.getDueDate() != null && !task.getDueDate().isAfter(task.getStartDate())) {
			errors.add("Due date must be after start date.");
		}

		// Overlap check
		if (task.getParticipants() != null && task.getStartDate() != null && task.getDueDate() != null) {
			// Get camp of the first participant
			CampDTO camp = task.getParticipants().stream().map(ParticipantDTO::getCamp).filter(Objects::nonNull).findFirst().orElse(null);

			if (camp != null) {
				for (TaskDTO existing : findAllTasksByCamp(camp)) {
					if (existing == null) continue;
					if (task.getId() != null && existing.getId() != null && Objects.equals(task.getId(), existing.getId())) continue;
					if (existing.getParticipants() == null || existing.getStartDate() == null || existing.getDueDate() == null) continue;

					boolean sharesParticipant = false;
					for (ParticipantDTO p : task.getParticipants()) {
						for (ParticipantDTO ep : existing.getParticipants()) {
							if (ep != null && Objects.equals(p.getId(), ep.getId())) { sharesParticipant = true; break; }
						}
						if (sharesParticipant) break;
					}
					if (!sharesParticipant) continue;

					if (task.getStartDate().isBefore(existing.getDueDate()) && task.getDueDate().isAfter(existing.getStartDate())) {
						errors.add("Task time overlaps with another task for at least one participant (task #" + existing.getId() + ").");
						break;
					}
				}
			}
		}

		if (!errors.isEmpty()) throw new ListException(errors);

		// Save person
		TaskDTO dto = taskMapper.toDTO(repository.save(taskMapper.toEntity(task)));
		repository.commit();

		return dto;
	}

	public void delete(int id) throws UnitOfWorkException {
		repository.delete(id);
		repository.commit();
	}
}
