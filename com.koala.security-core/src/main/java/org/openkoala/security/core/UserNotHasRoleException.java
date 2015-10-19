package org.openkoala.security.core;


public class UserNotHasRoleException extends SecurityException {

    private static final long serialVersionUID = -2862713476987456198L;

    public UserNotHasRoleException() {
        super();
    }

    public UserNotHasRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotHasRoleException(String message) {
        super(message);
    }

    public UserNotHasRoleException(Throwable cause) {
        super(cause);
    }

}
