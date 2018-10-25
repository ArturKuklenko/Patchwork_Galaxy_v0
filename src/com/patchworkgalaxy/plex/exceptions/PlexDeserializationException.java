package com.patchworkgalaxy.plex.exceptions;

/**
 * Thrown to indicate an error deserializing a Plex context.
 * @author redacted
 */
public class PlexDeserializationException extends PlexException {
    private static final long serialVersionUID = 1L;
    
    public PlexDeserializationException() {}
    
    public PlexDeserializationException(String message) {
	super(message);
    }
    
    public PlexDeserializationException(Throwable cause) {
	super(cause);
    }
    
    public PlexDeserializationException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
