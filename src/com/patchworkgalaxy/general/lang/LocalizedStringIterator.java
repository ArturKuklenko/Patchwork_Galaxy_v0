package com.patchworkgalaxy.general.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

class LocalizedStringIterator implements LocalizedString {
    
    private final List<String> _values;
    private Iterator<String> _iterator;
    private final boolean _isRandom;
    
    LocalizedStringIterator(Collection<String> values, boolean randomize) {
	if(values.isEmpty()) throw new IllegalArgumentException("Empty collection passed to localized value constructor");
	_values = new ArrayList<>();
	_values.addAll(values);
	_isRandom = randomize;
	//dummy iterator so we don't have to test for nulls in resetIterator
	_iterator = new Iterator<String>() {
	    @Override public boolean hasNext() { return false; }
	    @Override public String next() { throw new NoSuchElementException(); }
	    @Override public void remove() { throw new UnsupportedOperationException(); }
	};
    }

    private void resetIterator(Random rng) {
	if(_isRandom)
	    Collections.shuffle(_values, rng);
	_iterator = _values.iterator();
    }
    
    @Override public String getLocalizedValue(Random rng) {
	if(!_iterator.hasNext())
	    resetIterator(rng);
	return _iterator.next();
    }

    @Override public boolean isMultiple() {
	return true;
    }
    
}
