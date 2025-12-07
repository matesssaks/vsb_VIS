package cz.vsb.and0153.campmanagement.user.mapper;

import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.entity.User;
import cz.vsb.and0153.campmanagement.user.record.UserRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserRecord, User> {
	@Override
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	User merge(@MappingTarget User into, User from);

	@Override
	UserRecord toRecord(User user);

	@Override
	User toEntity(UserRecord userRecord);

	UserDTO toDTO(User user);
	@Mapping(target = "password", ignore = true)
	User toEntity(UserDTO campDTO);
}
