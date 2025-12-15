package cz.vsb.and0153.campmanagement.participant;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.camp.repository.CampRepository;
import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
import cz.vsb.and0153.campmanagement.participant.mapper.ParticipantMapper;
import cz.vsb.and0153.campmanagement.participant.mapper.RoleMapper;
import cz.vsb.and0153.campmanagement.participant.repository.ParticipantRepository;
import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import cz.vsb.and0153.campmanagement.server.exception.ListException;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ParticipantService {

	private final ParticipantMapper participantMapper;
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final CampMapper campMapper;

	private final ParticipantRepository participantRepository;
	private final CampRepository campRepository;

	public ParticipantDTO findById(int id) {
		return participantMapper.toDTO(participantRepository.findById(id));
	}

	public List<ParticipantDTO> findAllByUser(UserDTO user) {
		return participantRepository.findAllByUser(userMapper.toEntity(user)).stream().map(participantMapper::toDTO).toList();
	}

	public List<ParticipantDTO> findAllByCamp(CampDTO camp) {
		return participantRepository.findAllByCamp(campMapper.toEntity(camp)).stream().map(participantMapper::toDTO).toList();
	}

	public ParticipantDTO register(PersonDTO person, CampDTO camp) throws UnitOfWorkException, ListException {

		// Check if the camp is full
		if (camp.getRegistered() >= camp.getCapacity()) {
			throw new ListException(List.of("Camp is full."));
		}

		ParticipantDTO participant = new ParticipantDTO();
		participant.setPerson(person);
		participant.setCamp(camp);
		participant.setRole(roleMapper.toDTO(participantRepository.getDefaultRole()));

		ParticipantDTO dto = participantMapper.toDTO(participantRepository.save(participantMapper.toEntity(participant)));

		Camp campEntity = campMapper.toEntity(camp);
		campEntity.setRegistered(campEntity.getRegistered() + 1);
		campRepository.save(campEntity);

		participantRepository.commit();
		return dto;
	}
}
