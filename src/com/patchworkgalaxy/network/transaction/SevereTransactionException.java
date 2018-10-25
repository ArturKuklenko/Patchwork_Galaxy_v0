package com.patchworkgalaxy.network.transaction;

public class SevereTransactionException extends TransactionException {
    private static final long serialVersionUID = 1L;
    
    public SevereTransactionException() {}
    
    public SevereTransactionException(String message) {
	super(message);
    }
    
    public SevereTransactionException(Throwable cause) {
	super(cause);
    }
    
    public SevereTransactionException(String message, Throwable cause) {
	super(message, cause);
    }
    
}
