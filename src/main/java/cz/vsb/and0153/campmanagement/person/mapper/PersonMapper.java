package cz.vsb.and0153.campmanagement.person.mapper;

import cz.vsb.and0153.campmanagement.person.dto.PersonDTO;
import cz.vsb.and0153.campmanagement.person.entity.Drug;
import cz.vsb.and0153.campmanagement.person.entity.Person;
import cz.vsb.and0153.campmanagement.person.gateway.DrugGateway;
import cz.vsb.and0153.campmanagement.person.record.PersonRecord;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import cz.vsb.and0153.campmanagement.user.mapper.UserMapper;
import cz.vsb.and0153.campmanagement.user.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
	componentModel = "spring",
	uses = {UserMapper.class, DrugMapper.class}
)
public abstract class PersonMapper implements EntityMapper<PersonRecord, Person> {

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	protected UserRepository userRepository;

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	protected DrugGateway drugGateway;

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	private DrugMapper drugMapper;

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Person merge(@MappingTarget Person into, Person from);

	@Override
	@Mapping(target = "userId", source = "user.id")
	public abstract PersonRecord toRecord(Person entity);

	protected List<Drug> getDrugsByPersonId(Integer personId) {
		return new ArrayList<>(drugGateway.findAllByPersonId(personId).stream().map(drugMapper::toEntity).toList());
	}

	@Override
	@Mapping(target = "user", expression = "java(userRepository.findById(rec.getUserId()))")
	@Mapping(target = "drugs", expression = "java(getDrugsByPersonId(rec.getId()))")
	public abstract Person toEntity(PersonRecord rec);

	public abstract PersonDTO toDTO(Person entity);
	public abstract Person toEntity(PersonDTO dto);
}
