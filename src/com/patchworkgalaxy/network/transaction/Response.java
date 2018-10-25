package com.patchworkgalaxy.network.transaction;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

@Serializable
public class Response extends AbstractMessage {
    
    private int _id;
    private Object _payload;
    private Response.Type _responseType;
    
    /**
     * @deprecated serialization only
     */
    @Deprecated public Response() {}
    
    Response(int id, Response.Type responseType, Object payload) {
	setReliable(true);
	_id = id;
	_payload = payload;
	_responseType = responseType;
    }
    
    public int getId() {
	return _id;
    }
    
    public Object getPayload() {
	return _payload;
    }
    
    public boolean isSuccess() {
	return _responseType == Response.Type.SUCCESS;
    }
    
    public boolean isError() {
	return _responseType == Response.Type.ERROR;
    }
    
    static enum Type {
	SUCCESS, FAILURE, ERROR;
    }
    
}
