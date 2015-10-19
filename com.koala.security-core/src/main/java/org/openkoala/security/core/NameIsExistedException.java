package org.openkoala.security.core;

public class NameIsExistedException extends SecurityException {

    private static final long serialVersionUID = 5085396915916927288L;

    public NameIsExistedException() {
        super();
    }

    public NameIsExistedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NameIsExistedException(String message) {
        super(message);
    }

    public NameIsExistedException(Throwable cause) {
        super(cause);
    }

}
