package com.patchworkgalaxy.plex.exceptions;

import com.patchworkgalaxy.general.util.SignallingException;

/**
 * Supertype for all Plex exceptions.
 * @author redacted
 */
public abstract class PlexException extends SignallingException {
    private static final long serialVersionUID = 1L;
    
    public PlexException() {}
    
    public PlexException(String message) {
	super(message);
    }
    
    public PlexException(Throwable cause) {
	super(cause);
    }
    
    public PlexException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
