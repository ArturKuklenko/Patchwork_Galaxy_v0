package com.patchworkgalaxy.plex.exceptions;

/**
 * Thrown to indicate illegal record accession, due to either accessing an
 * unknown record, or accessing an unknown key of a valid record. This is used
 * whether the accession key is an int (eg. internal access from a Plex
 * expression) or a String (eg. external access by a public method of
 * {@link Context}).
 * @author redacted
 */
public class PlexRecordException extends PlexException {
    private static final long serialVersionUID = 1L;
    
    public PlexRecordException() {}
    
    public PlexRecordException(String message) {
	super(message);
    }
    
    public PlexRecordException(Throwable cause) {
	super(cause);
    }
    
    public PlexRecordException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
