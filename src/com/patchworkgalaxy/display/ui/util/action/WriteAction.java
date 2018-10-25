package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import java.util.concurrent.Callable;

public class WriteAction extends Action {
    
    private final Property.Type[] _properties;
    private final Object _value;
    
    @SuppressWarnings("unchecked")
    public WriteAction(Object value, Property.Type... properties) {
	_properties = properties;
	_value = value;
    }
    
    @SuppressWarnings("unchecked")
    @Override public void act(Component actOn) {
	actOn.write(_value, _properties);
    }
    
    @SuppressWarnings("unchecked")
    @Override public Callable<Void> asCallable(final Component component) {
	return new Callable<Void>() {
	    @Override public Void call() throws Exception {
		float duration = component.write(_value, _properties);
		Thread.sleep((int)(duration * 1000));
		return null;
	    }
	};
    }
    
}
