package com.patchworkgalaxy.network.transaction;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * A request from a client for the server to perform some action and/or respond
 * with some data. The client must include a unique (from the client's
 * perspective) integer ID. Any response from the server will
 * include the same ID.
 * @author redacted
 */
@Serializable
public class NetTransaction extends AbstractMessage {
    
    private int _id;
    private TransactionType _type;
    private String[] _args;
    
    /**
     * @deprecated serialization only
     */
    @Deprecated public NetTransaction() {}
    
    public NetTransaction(int id, TransactionType type, String[] args) {
	setReliable(true);
	_args = type.validate(args);
	_id = id;
	_type = type;
    }
    
    public int getId() {
	return _id;
    }
    
    public TransactionType getRequestType() {
	return _type;
    }
    
    public Response getSuccessfulResponse(Object payload) {
	return new Response(_id, Response.Type.SUCCESS, payload);
    }
    
    public Response getFailureResponse(Object payload) {
	return new Response(_id, Response.Type.FAILURE, payload);
    }
    
    public Response getErrorResponse(Throwable payload) {
	return new Response(_id, Response.Type.ERROR, payload.getLocalizedMessage());
    }
    
    public boolean hasArgument(TransactionArgumentKey argument) {
	return _type.getArgumentIndex(argument) >= 0;
    }
    
    public String getArgument(TransactionArgumentKey argument) {
	try {
	    return getArgument(argument, false);
	} catch(TransactionException e) {
	    return null;
	}
    }
    
    public String getArgument(TransactionArgumentKey argument, boolean validate) throws TransactionException {
	if(!hasArgument(argument))
	    return null;
	String value = _args[_type.getArgumentIndex(argument)];
	if(value == null)
	    value = "";
	if(validate)
	    argument.validate(value);
	return value;
    }
    
}
