package com.patchworkgalaxy.network.server;

import com.jme3.math.ColorRGBA;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import com.patchworkgalaxy.display.oldui.ColoredText;

@Serializable
public class ChatMessage extends AbstractMessage {
    
    String sender;
    String message;
    String color;
    boolean nocolon;
    
    public ChatMessage() { }
    
    public ChatMessage(String message) {
	this("", message, "");
    }
    
    public ChatMessage(String sender, String message) {
	this(sender, message, "");
    }
    
    public ChatMessage(String sender, String message, String color) {
	this.sender = sender;
	this.message = message;
	this.color = color;
    }
    
    public static ColorRGBA parseColor(String colorString) {
	int rx = Integer.valueOf(colorString.substring(0, 2), 16);
	int gx = Integer.valueOf(colorString.substring(2, 4), 16);
	int bx = Integer.valueOf(colorString.substring(4, 6), 16);
	float r = ((float)rx)/255f;
	float g = ((float)gx)/255f;
	float b = ((float)bx)/255f;
	return new ColorRGBA(r, g, b, 1);
    }
    
    public String getMessage() {
	return message;
    }
    
    public String getSender() {
	return sender;
    }
    
    public String getColor() {
	return color == null ? "" : color;
    }
    
    public void setSender(String sender) {
	this.sender = sender;
    }
    
    public void setMessage(String message) {
	this.message = message;
    }
    
    public void setColor(String color) {
	this.color = color;
    }
    
    public ChatMessage suppressColon() {
	nocolon = true;
	return this;
    }
    
    public boolean hasColon() {
	return !nocolon;
    }
    
    public ColoredText getColoredText() {
	if(color == null || color.length() == 0)
	    return new ColoredText(sender + message);
	ColoredText result = new ColoredText();
	if(sender.length() > 0) {
	    result.addText(sender, parseColor(color));
	    result.addText(message);
	}
	else
	    result.addText(message, parseColor(color));
	return result;
    }
    
}
