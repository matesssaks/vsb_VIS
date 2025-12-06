package cz.vsb.and0153.campmanagement.user.entity;

import cz.vsb.and0153.campmanagement.server.security.AuthUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
public class User implements AuthUser {
	Integer id;
	String name;
	String email;
	String password;

	@Override
	public Collection<String> getPermissions() {
		return AuthUser.super.getPermissions();
	}
}
