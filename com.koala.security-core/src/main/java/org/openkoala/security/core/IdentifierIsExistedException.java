package org.openkoala.security.core;

public class IdentifierIsExistedException extends SecurityException {

    private static final long serialVersionUID = -8131946271128084360L;

    public IdentifierIsExistedException() {
        super();
    }

    public IdentifierIsExistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentifierIsExistedException(String message) {
        super(message);
    }

    public IdentifierIsExistedException(Throwable cause) {
        super(cause);
    }

}
