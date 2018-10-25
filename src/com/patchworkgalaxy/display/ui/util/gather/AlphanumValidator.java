package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;
import java.util.regex.Pattern;

public final class AlphanumValidator extends ComponentValidator {
    
    private static final Pattern ALPHA_NUMERIC = Pattern.compile("^\\w*$");
    
    public AlphanumValidator() {}
    
    public AlphanumValidator(boolean selects) {
	super(selects);
    }    

    @Override protected boolean validate(Component component) {
	String text = component.getInputText();
	if(text == null) return true;
	return ALPHA_NUMERIC.matcher(text).find();
    }
    
}
