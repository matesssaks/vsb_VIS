package cz.vsb.and0153.campmanagement.task.service;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.task.dto.TaskDTO;
import cz.vsb.and0153.campmanagement.task.mapper.TaskMapper;
import cz.vsb.and0153.campmanagement.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository repository;

	private final TaskMapper taskMapper;
	private final CampMapper campMapper;

	public TaskDTO findById(int id) {
		return taskMapper.toDTO(repository.findById(id));
	}

	public List<TaskDTO> findAllTasksByCamp(CampDTO camp) {
		return repository.findAllByCampId(campMapper.toEntity(camp)).stream().map(taskMapper::toDTO).toList();
	}

	public List<TaskDTO> generateDrugPlan(CampDTO camp) {
		return this.findAllTasksByCamp(camp);
	}

	public TaskDTO save(TaskDTO task) throws ListException, UnitOfWorkException {
		List<String> errors = new ArrayList<>();

		// Validate if all fields are filled
		if (task.getShortDescription() == null || task.getShortDescription().isBlank()) errors.add("Short description is required.");
		if (task.getDescription() == null || task.getDescription().isBlank()) errors.add("Description is required.");
		if (task.getStartDate() == null) errors.add("Start date is required.");
		if (task.getDueDate() == null) errors.add("Due date is required.");
		if (task.getParticipants() == null || task.getParticipants().isEmpty()) errors.add("Participants are required.");

		if (!errors.isEmpty()) throw new ListException(errors);

		// Save person
		TaskDTO dto = taskMapper.toDTO(repository.save(taskMapper.toEntity(task)));
		repository.commit();

		return dto;
	}
}
