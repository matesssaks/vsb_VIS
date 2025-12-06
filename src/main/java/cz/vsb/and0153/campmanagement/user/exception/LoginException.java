package cz.vsb.and0153.campmanagement.user.exception;

import lombok.Getter;

import java.util.List;

public class LoginException extends Exception {

	@Getter
	private final List<String> errors;

	public LoginException(List<String> errors) {
		super();
		this.errors = errors;
	}
}
