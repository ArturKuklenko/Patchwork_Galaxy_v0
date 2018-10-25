package com.patchworkgalaxy.general.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TypedList<T> implements List<T> {

    private final List<T> _list;
    private final Class<T> _type;
    
    /**
     * Constructs a TypedList backed by an ArrayList.
     * @param type
     */
    public TypedList(Class<T> type) {
	this(new ArrayList<T>(), type);
    }
    
    /**
     * Constructs a TypedList backed by a given list.
     * @param list the backing list
     * @param type 
     */
    public TypedList(List<T> list, Class<T> type) {
	
	if(list == null) throw new IllegalArgumentException();
	
	_list = list;
	_type = type;
    }
    
    /**
     * Constructs a TypedList that is a copy of another TypedList.
     * @param copyOf the list to copy
     */
    public TypedList(TypedList<T> copyOf) {
	this(new ArrayList<>(copyOf._list), copyOf._type);
    }
    
    @Override
    public int size() {
	return _list.size();
    }

    @Override
    public boolean isEmpty() {
	return _list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
	return _list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
	return _list.iterator();
    }

    @Override
    public Object[] toArray() {
	return _list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
	return _list.toArray(a);
    }

    @Override
    public boolean add(T e) {
	return _list.add(e);
    }

    @Override
    public boolean remove(Object o) {
	return _list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	return _list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
	return _list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
	return _list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	return _list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	return _list.retainAll(c);
    }

    @Override
    public void clear() {
	_list.clear();
    }

    @Override
    public T get(int index) {
	return _list.get(index);
    }

    @Override
    public T set(int index, T element) {
	return _list.set(index, element);
    }

    @Override
    public void add(int index, T element) {
	_list.add(index, element);
    }

    @Override
    public T remove(int index) {
	return _list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
	return _list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
	return _list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
	return _list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
	return _list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
	return _list.subList(fromIndex, toIndex);
    }
    
    public List<T> getList() {
	return _list;
    }
    
    @SuppressWarnings("unchecked")
    public <T> TypedList<T> asListOfType(Class<T> type) {
	if(_type.isAssignableFrom(type))
	    return (TypedList<T>)this;
	else
	    throw new ClassCastException("Type " + type + " requested but have type " + _type);
    }
    
    public Class<T> getType() {
	return _type;
    }
    
}
