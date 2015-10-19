package org.openkoala.security.core;

/**
 * 对象关联关系异常
 * 
 * @author lucas
 */
public class CorrelationException extends SecurityException{

	private static final long serialVersionUID = -8386993126065565306L;

	public CorrelationException() {
		super();
	}

	public CorrelationException(String message, Throwable cause) {
		super(message, cause);
	}

	public CorrelationException(String message) {
		super(message);
	}

	public CorrelationException(Throwable cause) {
		super(cause);
	}
	
}
