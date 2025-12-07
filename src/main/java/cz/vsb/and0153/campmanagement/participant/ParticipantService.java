package cz.vsb.and0153.campmanagement.participant;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
import cz.vsb.and0153.campmanagement.participant.mapper.ParticipantMapper;
import cz.vsb.and0153.campmanagement.participant.mapper.RoleMapper;
import cz.vsb.and0153.campmanagement.participant.repository.ParticipantRepository;
import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import cz.vsb.and0153.campmanagement.server.uow.UnitOfWorkException;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

	private final ParticipantMapper participantMapper;
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final CampMapper campMapper;

	private final ParticipantRepository repository;

	public ParticipantDTO findById(int id) {
		return participantMapper.toDTO(repository.findById(id));
	}

	public List<ParticipantDTO> findAllByUser(UserDTO user) {
		return repository.findAllByUser(userMapper.toEntity(user)).stream().map(participantMapper::toDTO).toList();
	}

	public List<ParticipantDTO> findAllByCamp(CampDTO camp) {
		return repository.findAllByCamp(campMapper.toEntity(camp)).stream().map(participantMapper::toDTO).toList();
	}

	public ParticipantDTO register(PersonDTO person, CampDTO camp) throws UnitOfWorkException {
		ParticipantDTO participant = new ParticipantDTO();
		participant.setPerson(person);
		participant.setCamp(camp);
		participant.setRole(roleMapper.toDTO(repository.getDefaultRole()));

		ParticipantDTO dto = participantMapper.toDTO(repository.save(participantMapper.toEntity(participant)));
		repository.commit();
		return dto;
	}
}
