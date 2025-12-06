package cz.vsb.and0153.campmanagement.server.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface AuthUser extends Serializable {
	default Collection<String> permissions() {
		return List.of("*");
	}

	String getUsername();

	static AuthUser getUser(HttpServletRequest req) {
		HttpSession session = req.getSession(false);

		if (session == null) return UnregisteredUser.INSTANCE;

		if (session.getAttribute("user") instanceof AuthUser user) return user;

		return UnregisteredUser.INSTANCE;
	}

	static void setUser(HttpServletRequest req, AuthUser user) {
		req.getSession(true).setAttribute("user", user);
	}
}
