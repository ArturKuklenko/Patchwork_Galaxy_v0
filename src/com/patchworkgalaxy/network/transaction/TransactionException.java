package com.patchworkgalaxy.network.transaction;

public class TransactionException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public TransactionException() {}
    
    public TransactionException(String message) {
	super(message);
    }
    
    public TransactionException(Throwable cause) {
	super(cause);
    }
    
    public TransactionException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
