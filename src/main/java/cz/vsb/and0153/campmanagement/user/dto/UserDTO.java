package cz.vsb.and0153.campmanagement.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
	Integer id;
	String name;
	String email;
	String password;
}
