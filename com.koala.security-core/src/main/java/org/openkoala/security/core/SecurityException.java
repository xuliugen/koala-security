package org.openkoala.security.core;

public class SecurityException extends RuntimeException {

	private static final long serialVersionUID = 2631317949499926794L;

	public SecurityException() {
	}

	public SecurityException(String message) {
		super(message);
	}

	public SecurityException(Throwable cause) {
		super(cause);
	}

	public SecurityException(String message, Throwable cause) {
		super(message, cause);
	}
}
