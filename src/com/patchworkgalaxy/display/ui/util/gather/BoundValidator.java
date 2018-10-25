package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;

class BoundValidator {
    
    private final ComponentValidator _validator;
    private final String _key;
    
    BoundValidator(ComponentValidator validator, String key) {
	_validator = validator;
	_key = key;
    }
    
    Component validate(Panel[] panels) {
	return _validator.validate(panels, _key);
    }
    
    String getKey() {
	return _key;
    }
    
}
