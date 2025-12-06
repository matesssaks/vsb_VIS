package cz.vsb.and0153.campmanagement.user.dto;

import cz.vsb.and0153.campmanagement.server.security.AuthUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO implements AuthUser {
	Integer id;
	String name;
	String email;

	@Override
	public Collection<String> permissions() {
		return List.of("*", "logged");
	}

	@Override
	public String getUsername() {
		return name;
	}
}
