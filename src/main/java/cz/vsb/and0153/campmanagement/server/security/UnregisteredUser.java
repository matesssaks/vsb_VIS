package cz.vsb.and0153.campmanagement.server.security;

import lombok.NoArgsConstructor;

import java.util.Collection;

@SuppressWarnings("java:S6548") // Allow using as singleton
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UnregisteredUser implements AuthUser {

	public static final UnregisteredUser INSTANCE = new UnregisteredUser();

	@Override
	public Collection<String> permissions() {
		return AuthUser.super.permissions();
	}

	@Override
	public String getUsername() {
		return "anonymous";
	}
}
