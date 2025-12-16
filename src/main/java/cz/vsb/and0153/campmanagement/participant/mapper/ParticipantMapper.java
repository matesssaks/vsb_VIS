package cz.vsb.and0153.campmanagement.participant.mapper;

import cz.vsb.and0153.campmanagement.camp.mapper.CampMapper;
import cz.vsb.and0153.campmanagement.camp.repository.CampRepository;
import cz.vsb.and0153.campmanagement.participant.dto.ParticipantDTO;
import cz.vsb.and0153.campmanagement.participant.entity.Participant;
import cz.vsb.and0153.campmanagement.participant.gateway.RoleGateway;
import cz.vsb.and0153.campmanagement.participant.record.ParticipantRecord;
import cz.vsb.and0153.campmanagement.person.mapper.PersonMapper;
import cz.vsb.and0153.campmanagement.person.repository.PersonRepository;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
	componentModel = "spring",
	uses = {CampMapper.class, PersonMapper.class, RoleMapper.class}
)
public abstract class ParticipantMapper implements EntityMapper<ParticipantRecord, Participant> {

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	protected CampRepository campRepository;

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	protected PersonRepository personRepository;

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	protected RoleGateway roleGateway;

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "person", expression = "java(from.getPerson())")
	public abstract Participant merge(@MappingTarget Participant into, Participant from);

	@Override
	@Mapping(target = "campId", source = "camp.id")
	@Mapping(target = "personId", source = "person.id")
	@Mapping(target = "roleId", source = "role.id")
	public abstract ParticipantRecord toRecord(Participant entity);

	@Override
	@Mapping(target = "camp", expression = "java(campRepository.findById(rec.getCampId()))")
	@Mapping(target = "person", expression = "java(personRepository.findById(rec.getPersonId()))")
	@Mapping(target = "role", expression = "java(roleMapper.toEntity(roleGateway.findById(rec.getRoleId())))")
	public abstract Participant toEntity(ParticipantRecord rec);

	public abstract ParticipantDTO toDTO(Participant entity);
	public abstract Participant toEntity(ParticipantDTO dto);
}
