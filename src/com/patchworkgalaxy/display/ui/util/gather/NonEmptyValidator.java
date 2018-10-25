package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;

public final class NonEmptyValidator extends ComponentValidator {

    public NonEmptyValidator() {
	this(true);
    }
    
    public NonEmptyValidator(boolean selects) {
	super(selects);
    }
    
    @Override protected boolean validate(Component component) {
	String text = component.getInputText();
	return(text != null && !text.isEmpty());
    }
    
}
