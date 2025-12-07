package cz.vsb.and0153.campmanagement.camp.mapper;

import cz.vsb.and0153.campmanagement.camp.dto.CampDTO;
import cz.vsb.and0153.campmanagement.camp.entity.Camp;
import cz.vsb.and0153.campmanagement.camp.record.CampRecord;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CampMapper extends EntityMapper<CampRecord, Camp> {
	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Camp merge(@MappingTarget Camp into, Camp from);

	@Override
	CampRecord toRecord(Camp camp);
	@Override
	Camp toEntity(CampRecord campRecord);

	CampDTO toDTO(Camp camp);
	@Mapping(target = "registered", ignore = true)
	Camp toEntity(CampDTO campDTO);
}
