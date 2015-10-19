package org.openkoala.security.core;

public class UrlIsExistedException extends SecurityException {

	private static final long serialVersionUID = 1430763683336291150L;

	public UrlIsExistedException() {
		super();
	}

	public UrlIsExistedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UrlIsExistedException(String s) {
		super(s);
	}

	public UrlIsExistedException(Throwable cause) {
		super(cause);
	}

	
}
