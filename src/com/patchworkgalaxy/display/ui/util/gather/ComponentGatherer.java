package com.patchworkgalaxy.display.ui.util.gather;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComponentGatherer {
    
    private final List<BoundValidator> _validators;
    
    public ComponentGatherer() {
	_validators = new ArrayList<>();
    }
    
    public ComponentGatherer gathers(String key, ComponentValidator validator) {
	if(validator == null) validator = TrueValidator.MAIN;
	_validators.add(new BoundValidator(validator, key));
	return this;
    }
    
    public Set<Component> gatherComponents(Panel... panels) {
	Set<Component> components = new HashSet<>();
	Component component;
	for(BoundValidator validator : _validators) {
	    component = validator.validate(panels);
	    if(component == null) return new HashSet<>();
	    components.add(component);
	}
	return components;
    }
    
    public Map<String, String> gatherStrings(Panel... panels) {
	Map<String, String> result = new HashMap<>();
	Component component;
	for(BoundValidator validator : _validators) {
	    component = validator.validate(panels);
	    if(component == null) return new HashMap<>();
	    result.put(validator.getKey(), component.getInputText());
	}
	return result;
    }
    
}
