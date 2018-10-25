package com.patchworkgalaxy.general.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class FixedIterator<T> implements Iterator<T> {
    
    private final LinkedList<T> _list;
    
    public FixedIterator(Collection<T> from) {
	_list = new LinkedList<>();
	_list.addAll(from);
    }

    @Override
    public boolean hasNext() {
	return !_list.isEmpty();
    }

    @Override
    public T next() {
	return _list.removeFirst();
    }

    @Override
    public void remove() {
	throw new UnsupportedOperationException();
    }
    
    
    
}
