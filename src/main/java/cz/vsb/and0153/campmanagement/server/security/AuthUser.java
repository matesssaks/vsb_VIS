package cz.vsb.and0153.campmanagement.server.security;

import jakarta.servlet.http.HttpSession;

import java.util.Collection;
import java.util.List;

public interface AuthUser {
	default Collection<String> getPermissions() {
		return List.of("*");
	}

	static AuthUser getUser(HttpSession session) {
		if (session == null) return UnregisteredUser.INSTANCE;

		if (session.getAttribute("user") instanceof AuthUser user) return user;

		return UnregisteredUser.INSTANCE;
	}
}
