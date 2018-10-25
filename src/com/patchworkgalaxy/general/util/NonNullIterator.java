package com.patchworkgalaxy.general.util;

import java.util.Iterator;

public class NonNullIterator<T> implements Iterator<T> {
    
    private final Iterator<T> _base;
    private T _next;
    
    public NonNullIterator(Iterator<T> base) {
	_base = base;
	updateNext();
    }

    private void updateNext() {
	T next = null;
	while(next == null && _base.hasNext())
	    next = _base.next();
	_next = next;
    }
    
    @Override
    public boolean hasNext() {
	return _next != null;
    }

    @Override
    public T next() {
	T next = _next;
	updateNext();
	return next;
    }

    @Override
    public void remove() {
	_base.remove();
    }
    
}
