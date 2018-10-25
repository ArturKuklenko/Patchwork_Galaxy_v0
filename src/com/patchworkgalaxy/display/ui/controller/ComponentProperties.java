package com.patchworkgalaxy.display.ui.controller;

import com.patchworkgalaxy.display.oldui.UX2DControl;
import java.util.HashMap;
import java.util.Map;

class ComponentProperties {
    
    private final Map<Property.Type, Property> _properties;
    private final Map<Property.Type, Float> _durations;
    
    ComponentProperties(UX2DControl oldControl, Map<Property.Type, Float> durations) {
	_properties = new HashMap<>();
	for(Property.Type type : Property.Type.values()) {
	    if(durations.containsKey(type))
		_properties.put(type, type.getProperty(oldControl, durations.get(type)));
	    else
		_properties.put(type, type.getProperty(oldControl, 0));
	}
	_durations = durations;
    }
    
    @SuppressWarnings("unchecked")
    <T> float write(Property.Type<T> key, T value) {
	_properties.get(key).write(value);
	if(_durations.containsKey(key))
	    return _durations.get(key);
	else
	    return 0;
    }
    
}
