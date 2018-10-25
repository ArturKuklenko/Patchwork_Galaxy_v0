package com.patchworkgalaxy.network.oldmessages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * @deprecated old-style messaging
 */
@Serializable
public class ProtocolMessage extends AbstractMessage {
    
    String opcode, args;
    int id;
    Object payload;
    
    static int nextId;
    
    public ProtocolMessage() { }
    
    public ProtocolMessage(String message) {
	this(message.substring(0, 4), message.substring(4));
    }
    
    public ProtocolMessage(String opcode, String args) {
	this.opcode = opcode;
	this.args = args;
	setReliable(true);
    }
    
    public ProtocolMessage(String opcode, String args, int id) {
	this(opcode, args);
	this.id = id;
    }
    
    public ProtocolMessage(String opcode, int id, Object payload) {
	this.opcode = opcode;
	this.id = id;
	this.payload = payload;
	this.args = payload.toString();
    }
    
    public int getId() {
	if(id == 0)
	    id = ++nextId;
	return id;
    }
    
    public String getOpcode() {
	return opcode;
    }
    
    public String getArgs() {
	return args;
    }
    
    public String getArg(int index) {
	String[] argsarray = args.split(";;");
	if(index < argsarray.length)
	    return argsarray[index];
	else
	    return "";
    }
    
    public String getHumanMessage() {
	return args;
    }
    
    public String getMessage() {
	return opcode + args;
    }
    
    public Object getPayload() {
	return payload;
    }
    
}
