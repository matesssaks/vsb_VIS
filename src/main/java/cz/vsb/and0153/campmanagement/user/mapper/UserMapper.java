package cz.vsb.and0153.campmanagement.user.mapper;

import cz.vsb.and0153.campmanagement.user.dto.UserDTO;
import cz.vsb.and0153.campmanagement.user.entity.User;
import cz.vsb.and0153.campmanagement.user.record.UserRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserRecord toRecord(User user);
	User toEntity(UserRecord userRecord);

	UserDTO toDTO(User user);
	User toEntity(UserDTO userDTO);
}
