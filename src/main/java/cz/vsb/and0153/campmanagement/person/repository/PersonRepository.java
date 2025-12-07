package cz.vsb.and0153.campmanagement.person.repository;

import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.participant.entity.Participant;
import cz.vsb.and0153.campmanagement.participant.gateway.ParticipantGateway;
import cz.vsb.and0153.campmanagement.participant.record.ParticipantRecord;
import cz.vsb.and0153.campmanagement.participant.repository.ParticipantRepository;
import cz.vsb.and0153.campmanagement.person.entity.Drug;
import cz.vsb.and0153.campmanagement.person.entity.Person;
import cz.vsb.and0153.campmanagement.person.gateway.DrugGateway;
import cz.vsb.and0153.campmanagement.person.gateway.PersonGateway;
import cz.vsb.and0153.campmanagement.person.mapper.DrugMapper;
import cz.vsb.and0153.campmanagement.person.mapper.PersonMapper;
import cz.vsb.and0153.campmanagement.person.record.DrugRecord;
import cz.vsb.and0153.campmanagement.server.identitymap.IdentityMap;
import cz.vsb.and0153.campmanagement.server.repository.RepositoryInterface;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWork;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PersonRepository implements RepositoryInterface<Person> {

	private final ParticipantGateway participantGateway;

	private final PersonGateway personGateway;
	private final PersonMapper personMapper;
	private final DrugGateway drugGateway;
	private final DrugMapper drugMapper;

	private final IdentityMap identityMap;
	private final UnitOfWork unitOfWork;

	@Override
	public List<Person> findAll() {
		return unitOfWork.findAll(personMapper, personGateway);
	}

	@Override
	public Person findById(int id) {
		return unitOfWork.findById(Person.class, id, personMapper, personGateway);
	}

	public Person findByPersonalNumber(String personalNumber) {
		return identityMap.merge(personMapper.toEntity(personGateway.findByPersonalNumber(personalNumber)), personMapper);
	}

	public List<Person> findAllByUser(User user) {
		return personGateway.findAllByUserId(user.getId()).stream()
			.map(rec -> identityMap.merge(personMapper.toEntity(rec), personMapper)).toList();
	}

	@Override
	public Person save(Person entity) {
		if (entity.getDrugs() != null) {
			// Get IDs of stored drugs
			List<Integer> drugsToDelete = new ArrayList<>(
				drugGateway.findAllByPersonId(entity.getId()).stream().map(DrugRecord::getId).toList()
			);

			for (Drug drug : entity.getDrugs()) {
				drugsToDelete.remove(drug.getId()); // Keep only Drugs that haven't been deleted from the Person
				unitOfWork.save(drug, drugMapper, drugGateway); // Save updated Drug
			}

			// Delete Drugs that were deleted from the Person
			for (Integer removeId : drugsToDelete) {
				unitOfWork.delete(Drug.class, removeId, drugGateway);
			}
		}

		// Store parent entity (this will also load updated Drugs)
		return unitOfWork.save(entity, personMapper, personGateway);
	}

	@Override
	public void delete(int id) {
		Person entity = findById(id);

		// Delete children first
		for (Drug drug : entity.getDrugs()) {
			unitOfWork.delete(Drug.class, drug.getId(), drugGateway);
		}
		for (ParticipantRecord participant : participantGateway.findAllByPersonId(id)) {
			unitOfWork.delete(Participant.class, participant.getId(), participantGateway);
		}

		// Then delete parent
		unitOfWork.delete(Camp.class, id, personGateway);
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
