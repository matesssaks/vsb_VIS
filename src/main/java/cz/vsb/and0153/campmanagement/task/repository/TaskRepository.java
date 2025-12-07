package cz.vsb.and0153.campmanagement.task.repository;

import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.participant.entity.Participant;
import cz.vsb.and0153.campmanagement.server.identitymap.IdentityMap;
import cz.vsb.and0153.campmanagement.server.repository.RepositoryInterface;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWork;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.task.entity.Task;
import cz.vsb.and0153.campmanagement.task.gateway.ParticipantTaskGateway;
import cz.vsb.and0153.campmanagement.task.gateway.TaskGateway;
import cz.vsb.and0153.campmanagement.task.mapper.TaskMapper;
import cz.vsb.and0153.campmanagement.task.record.ParticipantTaskRecord;
import cz.vsb.and0153.campmanagement.task.record.TaskRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class TaskRepository implements RepositoryInterface<Task> {

	private final TaskGateway taskGateway;
	private final TaskMapper taskMapper;
	private final ParticipantTaskGateway participantTaskGateway;

	private final IdentityMap identityMap;
	private final UnitOfWork unitOfWork;

	@Override
	public List<Task> findAll() {
		return unitOfWork.findAll(taskMapper, taskGateway);
	}

	public List<Task> findAllByCampId(Camp camp) {
		return this.findAll().stream().filter(
			task -> task.getParticipants().stream().anyMatch(
				participant -> Objects.equals(participant.getCamp().getId(), camp.getId())
			)
		).toList();
	}

	@Override
	public Task findById(int id) {
		return unitOfWork.findById(Task.class, id, taskMapper, taskGateway);
	}

	@Override
	public Task save(Task entity) {
		TaskRecord rec = taskMapper.toRecord(entity);
		taskGateway.save(rec); // Force store of Task to generate ID

		if (entity.getParticipants() != null) {
			// Get IDs of stored participants
			List<Integer> participantsToDelete = new ArrayList<>(
				participantTaskGateway.findAllByTaskId(rec.getId()).stream().map(ParticipantTaskRecord::getParticipantId).toList()
			);

			for (Participant participant : entity.getParticipants()) {
				// Keep only Participants that haven't been deleted from the Task
				if (!participantsToDelete.remove(participant.getId())) {
					// If not yet stored, store the association
					ParticipantTaskRecord participantTaskRecord = new ParticipantTaskRecord();
					participantTaskRecord.setTaskId(rec.getId());
					participantTaskRecord.setParticipantId(participant.getId());
					participantTaskGateway.save(participantTaskRecord);
				}
			}

			// Delete Participants that were deleted from the Task
			for (Integer participantId : participantsToDelete) {
				participantTaskGateway.deleteByTaskIdAndParticipantId(rec.getId(), participantId);
			}
		}

		return identityMap.merge(taskMapper.toEntity(rec), taskMapper);
	}

	@Override
	public void delete(int id) {
		unitOfWork.delete(Camp.class, id, taskGateway);
	}

	@Override
	public void commit() throws UnitOfWorkException {
		unitOfWork.commit();
	}

	@Override
	public void rollback() {
		unitOfWork.rollback();
	}
}
