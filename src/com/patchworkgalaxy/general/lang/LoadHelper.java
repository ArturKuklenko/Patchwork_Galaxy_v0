package com.patchworkgalaxy.general.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LoadHelper {
    
    private final Map<String, List<String>> _values;
    private final Map<String, LocalizedStringMode> _modes;
    
    LoadHelper() {
	_values = new HashMap<>();
	_modes = new HashMap<>();
    }
    
    void addMapping(String key, String value) {
	try {
	    LocalizedStringMode mode = LocalizedStringMode.valueOf(value.toUpperCase());
	    _modes.put(key, mode);
	}
	catch(IllegalArgumentException e) {
	    getValues(key).add(value);
	}
    }
    
    Map<String, LocalizedString> getMappings() {
	Map<String, LocalizedString> result = new HashMap<>();
	for(String key : _values.keySet())
	    result.put(key, getLocalizedString(key));
	return result;
    }
    
    private List<String> getValues(String key) {
	if(!_values.containsKey(key)) {
	    List<String> result = new ArrayList<>();
	    _values.put(key, result);
	    return result;
	}
	return _values.get(key);
    }
    
    private LocalizedStringMode getMode(String key) {
	if(_modes.containsKey(key))
	    return _modes.get(key);
	return LocalizedStringMode.RANDOM_ITERATOR;
    }
    
    private LocalizedString getLocalizedString(String key) {
	return getMode(key).createLocalizedString(getValues(key));
    }
    
}
