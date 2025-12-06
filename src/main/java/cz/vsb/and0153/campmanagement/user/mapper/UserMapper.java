package cz.vsb.and0153.campmanagement.user.mapper;

import cz.vsb.and0153.campmanagement.server.mapper.EntityMapper;
import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.entity.User;
import cz.vsb.and0153.campmanagement.user.record.UserRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserRecord, User> {
	@Override
	UserRecord toRecord(User user);

	@Override
	User toEntity(UserRecord userRecord);

	@Override
	User merge(@MappingTarget User into, User from);

	UserDTO toDTO(User user);
	@Mapping(target = "password", ignore = true)
	User toEntity(UserDTO campDTO);
}
