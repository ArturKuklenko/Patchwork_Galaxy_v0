package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;

public class LengthValidator extends ComponentValidator {

    private final int _min, _max;
    
    public LengthValidator(int min, int max) {
	_min = min;
	_max = max;
    }
    
    public LengthValidator(int min, int max, boolean selects) {
	super(selects);
	_min = min;
	_max = max;
    }
    
    public static LengthValidator min(int min, boolean selects) {
	return new LengthValidator(min, Integer.MAX_VALUE, selects);
    }
    
    public static LengthValidator max(int max, boolean selects) {
	return new LengthValidator(0, max, selects);
    }
    
    public static LengthValidator min(int min) {
	return new LengthValidator(min, Integer.MAX_VALUE);
    }
    
    public static LengthValidator max(int max) {
	return new LengthValidator(0, max);
    }
    
    @Override protected boolean validate(Component component) {
	String text = component.getInputText();
	int len = text.length();
	return _min <= len && _max >= len;
    }
    
    
    
}
