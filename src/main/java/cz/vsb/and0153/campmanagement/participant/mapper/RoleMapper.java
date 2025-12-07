package cz.vsb.and0153.campmanagement.participant.mapper;

import cz.vsb.and0153.campmanagement.participant.dto.RoleDTO;
import cz.vsb.and0153.campmanagement.participant.entity.Role;
import cz.vsb.and0153.campmanagement.participant.record.RoleRecord;
import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleRecord, Role> {

	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	Role merge(@MappingTarget Role into, Role from);

	@Override
	RoleRecord toRecord(Role entity);
	@Override
	Role toEntity(RoleRecord rec);

	RoleDTO toDTO(Role entity);
	Role toEntity(RoleDTO dto);
}
