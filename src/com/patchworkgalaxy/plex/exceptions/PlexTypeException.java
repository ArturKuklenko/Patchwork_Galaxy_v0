package com.patchworkgalaxy.plex.exceptions;

/**
 * Thrown to indicate an improper attempt to use one Plex type as if it were
 * assignable as another when it isn't. The severity of this exception depends
 * on when it happens. During parsing, it is equivalent to a
 * {@linkplain PlexParseException parse exception} and indicates an invalid
 * program. In a running context, it may be thrown by
 * {@link Context#write(String, String, String)} to indicate that the written
 * value represents an invalid type, in which case the context is unchanged.
 * @author redacted
 */
public class PlexTypeException extends PlexException {
    private static final long serialVersionUID = 1L;
    
    public PlexTypeException() {}
    
    public PlexTypeException(String message) {
	super(message);
    }
    
    public PlexTypeException(Throwable cause) {
	super(cause);
    }
    
    public PlexTypeException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
