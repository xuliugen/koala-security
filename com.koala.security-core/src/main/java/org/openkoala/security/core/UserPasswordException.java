package org.openkoala.security.core;

public class UserPasswordException extends SecurityException {

    private static final long serialVersionUID = 5098261678688444370L;

    public UserPasswordException() {
        super();
    }

    public UserPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserPasswordException(String message) {
        super(message);
    }

    public UserPasswordException(Throwable cause) {
        super(cause);
    }

}
