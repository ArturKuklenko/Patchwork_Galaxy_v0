package com.patchworkgalaxy.general.util;

/**
 * A lightweight exception intended for signalling common error states.
 * For example, the server uses a signalling exception subclass to indicate
 * an invalid request from a user, such as an invalid login. Plex uses them
 * to indicate rule violations.
 * <p>
 * Besides semantics, a signalling exception is differentiated from normal
 * exceptions by its lack of a stack trace. This makes them significantly faster
 * than other kinds of exceptions.
 * </p><p>
 * Signalling exceptions are checked exceptions.
 * </p>
 * @author redacted
 */
public class SignallingException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public SignallingException() {}
    
    public SignallingException(String message) {
	super(message);
    }
    
    public SignallingException(Throwable cause) {
	super(cause);
    }
    
    public SignallingException(String message, Throwable cause) {
	super(message, cause);
    }
    
    /**
     * For efficiency, a signaling exception doesn't have a stack trace.
     * @return 
     */
    @Override
    public Throwable fillInStackTrace() {
	return this;
    }
    
}
