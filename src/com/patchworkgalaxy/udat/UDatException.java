package com.patchworkgalaxy.udat;

public class UDatException extends Exception {
    
    UDatException() {}
    
    UDatException(String message) {
	super(message);
    }
    
    UDatException(Throwable cause) {
	super(cause);
    }
    
    UDatException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
