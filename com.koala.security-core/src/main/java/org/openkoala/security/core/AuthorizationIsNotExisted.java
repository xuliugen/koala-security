package org.openkoala.security.core;

public class AuthorizationIsNotExisted extends SecurityException {

	private static final long serialVersionUID = -1550384549810021509L;

	public AuthorizationIsNotExisted() {
		super();
	}

	public AuthorizationIsNotExisted(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthorizationIsNotExisted(String message) {
		super(message);
	}

	public AuthorizationIsNotExisted(Throwable cause) {
		super(cause);
	}

}
