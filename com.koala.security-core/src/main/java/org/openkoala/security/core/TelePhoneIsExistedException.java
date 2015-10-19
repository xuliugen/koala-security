package org.openkoala.security.core;

public class TelePhoneIsExistedException extends SecurityException {

	private static final long serialVersionUID = 5363736963932226941L;

	public TelePhoneIsExistedException() {
		super();
	}

	public TelePhoneIsExistedException(String message, Throwable cause) {
		super(message, cause);
	}

	public TelePhoneIsExistedException(String message) {
		super(message);
	}

	public TelePhoneIsExistedException(Throwable cause) {
		super(cause);
	}

}
