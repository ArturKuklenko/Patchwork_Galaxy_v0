package com.patchworkgalaxy.general.lang;

import java.util.Collection;
import java.util.Random;

class LocalizedStringConstant implements LocalizedString {
    
    private final String _value;

    LocalizedStringConstant(Collection<String> values) {
	if(values.isEmpty()) throw new IllegalArgumentException("Empty collection passed to localized value constructor");
	_value = values.iterator().next();
    }
    
    @Override public String getLocalizedValue(Random rng) {
	return _value;
    }

    @Override public boolean isMultiple() {
	return false;
    }
    
}
