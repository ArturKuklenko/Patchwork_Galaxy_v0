package com.patchworkgalaxy.plex.exceptions;

/**
 * Thrown to indicate that an update violates a rule's {@code so-clause}.
 * @author redacted
 */
public class PlexRuleException extends PlexException {
    private static final long serialVersionUID = 1L;
    
    public PlexRuleException() {}
    
    public PlexRuleException(String message) {
	super(message);
    }
    
    public PlexRuleException(Throwable cause) {
	super(cause);
    }
    
    public PlexRuleException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
