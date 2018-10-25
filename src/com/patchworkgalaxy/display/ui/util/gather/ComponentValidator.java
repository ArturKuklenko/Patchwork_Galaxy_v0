package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;

public abstract class ComponentValidator {
    
    private final boolean _selects;
    
    protected ComponentValidator() {
	this(true);
    }
    
    protected ComponentValidator(boolean selects) {
	_selects = selects;
    }
    
    Component validate(Panel[] panels, String key) {
	Component component = null;
	for(Panel panel : panels) {
	    if(panel == null) continue;
	    component = panel.getComponent(key);
	    if(component != null) break;
	}
	if(component == null) throw new NullPointerException("Couldn't find a component at " + key + " (Searched " + panels.length + " panels)");
	if(!validate(component)) {
	    onInvalid(component);
	    return null;
	}
	return component;
    }
    
    protected abstract boolean validate(Component component);
    
    protected void onInvalid(Component component) {
	if(_selects)
	    component.focus();
    }
    
}
