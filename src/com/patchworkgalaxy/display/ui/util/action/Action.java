package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import java.util.concurrent.Callable;

public abstract class Action {
    
    public abstract void act(Component actOn);
    
    public ComponentCallback asCallback(final ComponentCallback.Type type) {
	return new ComponentCallback() {
	    @Override public void callback(Component component) {
		Action.this.act(component);
	    }
	    @Override public ComponentCallback.Type getType() {
		return type;
	    }
	    @Override public ComponentCallback getSecondaryCallback() {
		return null;
	    }
	    
	};
    }
    
    public Callable<Void> asCallable(final Component actOn) {
	return new Callable<Void>() {
	    @Override public Void call() throws Exception {
		act(actOn);
		return null;
	    }
	    
	};
    }
    
}
