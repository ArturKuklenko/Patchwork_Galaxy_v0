package com.patchworkgalaxy.display.ui.descriptors;

import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.ui.controller.Component;

public interface ComponentCallback {
    
    void callback(Component component);
    
    Type getType();
    
    ComponentCallback getSecondaryCallback();
    
    static enum Type {
	MOUSE_IN(CallbackType.MOUSE_IN),
	MOUSE_OUT(CallbackType.MOUSE_OUT),
	MOUSE_CLICK(CallbackType.CLICK),
	TICK(CallbackType.TICK),
	UPDATE(CallbackType.OBSERVE),
	SUBMIT(CallbackType.SUBMIT),
	INITIALIZE(CallbackType.INITIALIZE);
	
	private final CallbackType _oldType;
	Type(CallbackType oldType) {
	    _oldType = oldType;
	}
	
	public CallbackType getOldType() {
	    return _oldType;
	}
	
    }
    
}
