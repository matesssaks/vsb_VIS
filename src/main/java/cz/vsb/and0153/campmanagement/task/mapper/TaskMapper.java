package cz.vsb.and0153.campmanagement.task.mapper;

import cz.vsb.and0153.campmanagement.participant.entity.Participant;
import cz.vsb.and0153.campmanagement.participant.mapper.ParticipantMapper;
import cz.vsb.and0153.campmanagement.participant.repository.ParticipantRepository;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import cz.vsb.and0153.campmanagement.task.dto.TaskDTO;
import cz.vsb.and0153.campmanagement.task.entity.Task;
import cz.vsb.and0153.campmanagement.task.gateway.ParticipantTaskGateway;
import cz.vsb.and0153.campmanagement.task.record.TaskRecord;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
	componentModel = "spring",
	uses = {ParticipantMapper.class}
)
public abstract class TaskMapper implements EntityMapper<TaskRecord, Task> {

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	private ParticipantRepository participantRepository;

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	private ParticipantTaskGateway participantTaskGateway;

	@Override
	//@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Task merge(@MappingTarget Task into, Task from);

	// toRecord

	protected List<Participant> getParticipantsByTask(TaskRecord task) {
		return new ArrayList<>(participantTaskGateway.findAllByTaskId(task.getId()).stream().map(
			participantTaskRecord -> participantRepository.findById(participantTaskRecord.getParticipantId())
		).toList());
	}

	@Override
	@Mapping(target = "participants", expression = "java(getParticipantsByTask(rec))")
	public abstract Task toEntity(TaskRecord rec);

	public abstract TaskDTO toDTO(Task entity);
	public abstract Task toEntity(TaskDTO dto);
}
