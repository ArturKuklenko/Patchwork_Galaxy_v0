package com.patchworkgalaxy.plex.exceptions;

/**
 * Thrown to indicate that something is not a legal Plex program.
 * @author redacted
 */
public class PlexParseException extends PlexException {
    private static final long serialVersionUID = 1L;
    
    public PlexParseException() {}
    
    public PlexParseException(String message) {
	super(message);
    }
    
    public PlexParseException(Throwable cause) {
	super(cause);
    }
    
    public PlexParseException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
