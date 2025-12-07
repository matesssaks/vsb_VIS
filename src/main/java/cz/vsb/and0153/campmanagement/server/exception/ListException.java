package cz.vsb.and0153.campmanagement.server.exception;

import lombok.Getter;

import java.util.List;

public class ListException extends Exception {

	@Getter
	private final List<String> errors;

	public ListException(List<String> errors) {
		super();
		this.errors = errors;
	}
}
