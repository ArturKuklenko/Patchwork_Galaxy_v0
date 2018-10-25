package com.patchworkgalaxy.general.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

class LocalizedStringRandom implements LocalizedString {
    
    private final List<String> _values;
    
    LocalizedStringRandom(Collection<String> values) {
	if(values.isEmpty()) throw new IllegalArgumentException("Empty collection passed to localized value constructor");
	_values = new ArrayList<>();
	_values.addAll(values);
    }
    
    @Override public String getLocalizedValue(Random rng) {
	return _values.get(rng.nextInt(_values.size()));
    }

    @Override
    public boolean isMultiple() {
	return true;
    }
    
}
