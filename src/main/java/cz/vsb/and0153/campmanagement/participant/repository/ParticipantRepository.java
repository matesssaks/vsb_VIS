package cz.vsb.and0153.campmanagement.participant.repository;

import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.participant.entity.Participant;
import cz.vsb.and0153.campmanagement.participant.entity.Role;
import cz.vsb.and0153.campmanagement.participant.gateway.ParticipantGateway;
import cz.vsb.and0153.campmanagement.participant.gateway.RoleGateway;
import cz.vsb.and0153.campmanagement.participant.mapper.ParticipantMapper;
import cz.vsb.and0153.campmanagement.participant.mapper.RoleMapper;
import cz.vsb.and0153.campmanagement.person.entity.Person;
import cz.vsb.and0153.campmanagement.server.identitymap.IdentityMap;
import cz.vsb.and0153.campmanagement.server.repository.RepositoryInterface;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWork;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ParticipantRepository implements RepositoryInterface<Participant> {

	private final ParticipantGateway participantGateway;
	private final ParticipantMapper participantMapper;
	private final RoleGateway roleGateway;
	private final RoleMapper roleMapper;

	private final IdentityMap identityMap;
	private final UnitOfWork unitOfWork;

	@Override
	public List<Participant> findAll() {
		return unitOfWork.findAll(participantMapper, participantGateway);
	}

	public List<Participant> findAllByUser(User user) {
		return participantGateway.findAllByUserId(user.getId()).stream()
			.map(rec -> identityMap.merge(participantMapper.toEntity(rec), participantMapper)).toList();
	}

	public List<Participant> findAllByPerson(Person person) {
		return participantGateway.findAllByPersonId(person.getId()).stream()
			.map(rec -> identityMap.merge(participantMapper.toEntity(rec), participantMapper)).toList();
	}

	@Override
	public Participant findById(int id) {
		return unitOfWork.findById(Participant.class, id, participantMapper, participantGateway);
	}

	public Role getDefaultRole() {
		// Get the default role with ID 1
		return unitOfWork.findById(Role.class, 1, roleMapper, roleGateway);
	}

	@Override
	public Participant save(Participant entity) {
		return unitOfWork.save(entity, participantMapper, participantGateway);
	}

	@Override
	public void delete(int id) {
		unitOfWork.delete(Camp.class, id, participantGateway);
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
