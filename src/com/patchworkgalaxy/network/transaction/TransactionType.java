package com.patchworkgalaxy.network.transaction;

import java.util.EnumMap;
import java.util.Map;

public enum TransactionType {
    
    //login and registration
    LOGIN(TransactionArgumentKey.USERNAME, TransactionArgumentKey.PASSWORD),
    BEGIN_REGISTRATION(TransactionArgumentKey.REGISTER_NAME, TransactionArgumentKey.PASSWORD, TransactionArgumentKey.EMAIL),
    FINISH_REGISTRATION(TransactionArgumentKey.SECURITY_TOKEN),
    //account management
    RESET_PASSWORD(TransactionArgumentKey.USERNAME, TransactionArgumentKey.EMAIL),
    REQUEST_PASSWORD_CHANGE(TransactionArgumentKey.PASSWORD, TransactionArgumentKey.CHANGE_PASSWORD_TO),
    REQUEST_EMAIL_CHANGE(TransactionArgumentKey.EMAIL, TransactionArgumentKey.CHANGE_EMAIL_TO),
    USE_SECURITY_TOKEN(TransactionArgumentKey.SECURITY_TOKEN),
    SET_USER_DATA(TransactionArgumentKey.UDAT_KEY, TransactionArgumentKey.UDAT_VALUE),
    
    //channels
    JOIN_CHANNEL(TransactionArgumentKey.CHANNEL_NAME, TransactionArgumentKey.CHANNEL_PASSWORD),
    CREATE_CHANNEL(TransactionArgumentKey.CHANNEL_NAME, TransactionArgumentKey.CHANNEL_PASSWORD),
    JOIN_PREVIOUS_CHANNEL(),
    //games
    JOIN_GAME(TransactionArgumentKey.CHANNEL_NAME, TransactionArgumentKey.CHANNEL_PASSWORD),
    CREATE_GAME(TransactionArgumentKey.CHANNEL_NAME, TransactionArgumentKey.CHANNEL_PASSWORD),
    START_GAME(),
    
    //pure queries
    LIST_PUBLIC_CHANNELS(),
    LIST_PUBLIC_GAMES(),
    DESCRIBE_USER(TransactionArgumentKey.USERNAME),
    
    ;
    
    
    private final TransactionArgumentKey[] _argNames;
    private final int _argc;
    
    TransactionType(TransactionArgumentKey... args) {
	_argNames = args;
	_argc = args.length;
    }
    
    public int getArgumentIndex(TransactionArgumentKey arg) {
	for(int i = 0; i < _argc; ++i) {
	    if(_argNames[i] == arg)
		return i;
	}
	return -1;
    }
    
    public String[] validate(String[] args) {
	if(isLastArgumentOptional() && _argc == args.length + 1) {
	    String[] args2 = new String[_argc];
	    System.arraycopy(args, 0, args2, 0, _argc - 1);
	    args2[_argc - 1] = "";
	    args = args2;
	}
	if(_argc != args.length)
	    throw new IllegalArgumentException(this + " expected " + _argc + " args, have  " + args.length);
	return args;
    }
    
    public Map<TransactionArgumentKey, String> parse(String[] args) {
	if(isLastArgumentOptional() && _argc == args.length + 1) {
	    String[] args2 = new String[_argc];
	    System.arraycopy(args, 0, args2, 0, _argc - 1);
	    args = args2;
	}
	if(_argc != args.length)
	    throw new IllegalArgumentException(this + " expected " + _argc + " args, have  " + args.length);
	Map<TransactionArgumentKey, String> parsed = new EnumMap<>(TransactionArgumentKey.class);
	for(int i = _argNames.length; --i >= 0;)
	    parsed.put(_argNames[i], args[i] == null ? "" : args[i]);
	return parsed;
    }
    
    public boolean isTargetsAccount() {
	return !(this == BEGIN_REGISTRATION || this == FINISH_REGISTRATION);
    }
    
    public boolean isTargetsChannel() {
	return this == JOIN_CHANNEL;
    }
    
    public boolean isTargetsGame() {
	return this == JOIN_GAME || this == START_GAME;
    }
    
    public boolean isLastArgumentOptional() {
	return
		this == JOIN_CHANNEL || this == CREATE_CHANNEL ||
		this == JOIN_GAME || this == CREATE_GAME ||
		this == LOGIN;
    }
    
}
