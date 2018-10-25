package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;

public class CombinedValidator extends ComponentValidator {
    
    private final ComponentValidator[] _combines;
    
    public CombinedValidator(ComponentValidator... combines) {
	super(false);
	_combines = combines;
    }
    
    public CombinedValidator(boolean selects, ComponentValidator... combines) {
	super(selects);
	_combines = combines;
    }

    @Override protected boolean validate(Component component) {
	for(ComponentValidator validator : _combines) {
	    if(!validator.validate(component)) {
		validator.onInvalid(component);
		return false;
	    }
	}
	return true;
    }
    
}
