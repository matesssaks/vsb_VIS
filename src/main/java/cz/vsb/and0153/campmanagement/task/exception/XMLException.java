package cz.vsb.and0153.campmanagement.task.exception;

public class XMLException extends RuntimeException {
	public XMLException(String message) {
		super(message);
	}

	public XMLException(String message, Exception e) {
		super(message, e);
	}
}
