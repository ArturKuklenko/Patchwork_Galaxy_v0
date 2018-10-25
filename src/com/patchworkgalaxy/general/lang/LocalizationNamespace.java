package com.patchworkgalaxy.general.lang;

import java.util.Map;
import java.util.Random;

class LocalizationNamespace {
    
    private final Map<String, LocalizedString> _values;
    
    LocalizationNamespace(Map<String, LocalizedString> values) {
	_values = values;
    }
    
    String getValue(Random rng, String key) {
	LocalizedString value = _values.get(key);
	if(value == null)
	    return null;
	return value.getLocalizedValue(rng);
    }
    
}
