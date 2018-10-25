package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;

public class TrueValidator extends ComponentValidator {
    
    static final TrueValidator MAIN = new TrueValidator();

    @Override protected boolean validate(Component component) {
	return true;
    }
    
}
