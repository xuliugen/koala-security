package org.openkoala.security.core;

public class EmailIsExistedException extends SecurityException {

    private static final long serialVersionUID = -581984046035303044L;

    public EmailIsExistedException() {
        super();
    }

    public EmailIsExistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailIsExistedException(String message) {
        super(message);
    }

    public EmailIsExistedException(Throwable cause) {
        super(cause);
    }

}
