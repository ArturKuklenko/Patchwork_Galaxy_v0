package com.patchworkgalaxy.network.transaction;

public enum TransactionArgumentKey {
    
    USERNAME,
    PASSWORD,
    EMAIL,
    REGISTER_NAME,
    CHANNEL_NAME,
    CHANNEL_PASSWORD,
    
    CHANGE_PASSWORD_TO,
    CHANGE_EMAIL_TO,
    SECURITY_TOKEN,
    
    UDAT_KEY,
    UDAT_VALUE;
    
    public void validate(String toValidate) throws TransactionException {
	if(_validation != null)
	    _validation.validate(toValidate);
    }
    private interface Validation {
	void validate(String toValidate) throws TransactionException;
    }
    private Validation _validation;
    
    static {
	USERNAME._validation = REGISTER_NAME._validation = new Validation() {
	    @Override public void validate(String toValidate) throws TransactionException {
		if(!toValidate.matches("^\\w{3,}$"))
		    throw new TransactionException("Invalid username: should be at least three alphanumeric characters");
	    }
	};

	PASSWORD._validation = CHANGE_PASSWORD_TO._validation = new Validation() {
	    @Override public void validate(String toValidate) throws TransactionException {
		if(toValidate.length() < 6)
		    throw new TransactionException("Invalid password: should be at least six characters");
	    }
	};

	EMAIL._validation = CHANGE_EMAIL_TO._validation = new Validation() {
	    @Override public void validate(String toValidate) throws TransactionException {
		if(toValidate.length() < 6 || !toValidate.contains("@") || !toValidate.contains("."))
		    throw new TransactionException("Invalid email address");
	    }
	};
	
	CHANNEL_NAME._validation = new Validation() {
	    @Override public void validate(String toValidate) throws TransactionException {
		if(toValidate.length() < 3)
		    throw new TransactionException("Invalid channel name: should be at least three characters");
	    }
	};
	
	SECURITY_TOKEN._validation = new Validation() {
	    @Override public void validate(String toValidate) throws TransactionException {
		if(toValidate.length() != 6)
		    throw new TransactionException("Security token should be six characters");
	    }
	};
    }
    
}