package cz.vsb.and0153.campmanagement.person.mapper;

import cz.vsb.and0153.campmanagement.person.dto.DrugDTO;
import cz.vsb.and0153.campmanagement.person.entity.Drug;
import cz.vsb.and0153.campmanagement.person.gateway.DrugGateway;
import cz.vsb.and0153.campmanagement.person.record.DrugRecord;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DrugMapper implements EntityMapper<DrugRecord, Drug> {

	@Autowired
	@SuppressWarnings({"java:S6813", "SpringJavaAutowiredFieldsWarningInspection"}) // We need Autowired annotation
	protected DrugGateway gateway;

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	public abstract Drug merge(@MappingTarget Drug into, Drug from);

	@Override
	@Mapping(target = "personId", expression = "java(gateway.findPersonById(entity.getId()))")
	public abstract DrugRecord toRecord(Drug entity);
	// toEntity

	public abstract DrugDTO toDTO(Drug entity);
	public abstract Drug toEntity(DrugDTO dto);
}
