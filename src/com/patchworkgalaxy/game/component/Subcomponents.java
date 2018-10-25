package com.patchworkgalaxy.game.component;

import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import com.patchworkgalaxy.general.util.FixedIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

    
class Subcomponents<T extends GameComponent> implements Iterable<T>, Subscriber<GameEvent>, Namespace {
    
    private final LinkedHashSet<T> _components;
    private final LinkedHashMap<String, Set<T>> _namedBuckets;
    
    Subcomponents() {
	_components = new LinkedHashSet<>();
	_namedBuckets = new LinkedHashMap<>();
    }
    
    void add(T component) {
	String name = component.getName();
	_components.add(component);
	Set<T> bucket = _namedBuckets.get(name);
	if(bucket == null) {
	    bucket = new LinkedHashSet<>();
	    _namedBuckets.put(name, bucket);
	}
	bucket.add(component);
	component.addSubscription(this);
    }
    
    void remove(T component) {
	if(_components.remove(component)) {
	    String name = component.getName();
	    Set<T> bucket = _namedBuckets.get(name);
	    bucket.remove(component);
	    if(bucket.isEmpty())
		_namedBuckets.remove(name);
	}
    }
    
    void removeEldest(String name) {
	Set<T> bucket = _namedBuckets.get(name);
	if(bucket != null) {
	    Iterator<T> i = bucket.iterator();
	    if(i.hasNext()) {
		T eldest = i.next();
		i.remove();
		_components.remove(eldest);
		if(bucket.isEmpty())
		    _namedBuckets.remove(name);
	    }
	}
    }
    
    Set<T> getAll() {
	return new HashSet<>(_components);
    }
    
    T getEldest(String name) {
	Set<T> bucket = _namedBuckets.get(name);
	if(bucket != null && !bucket.isEmpty())
	    return bucket.iterator().next();
	return null;
    }
    
    int size() {
	return _components.size();
    }

    int size(String name) {
	return _namedBuckets.get(name).size();
    }
    
    List<T> getUnique() {
	List<T> unique = new ArrayList<>();
	for(Set<T> bucket : _namedBuckets.values()) {
	    if(!bucket.isEmpty())
		unique.add(bucket.iterator().next());
	}
	return unique;
    }
    
    public Set<T> getNamedBucket(String name) {
	Set<T> set = _namedBuckets.get(name);
	return set == null ? new HashSet<T>() : new HashSet<>(set);
    }
    
    @Override public Iterator<T> iterator() {
	return new FixedIterator<>(_components);
    }

    /*@Override
    @SuppressWarnings("unchecked")
    public void update(Observable o, Object arg) {
	if(o instanceof GameComponent) {
	    if(!((GameComponent)o).alive())
		remove((T)o);
	}
    }*/

    @Override public Object lookup(String name) {
	return getEldest(name);
    }

    @SuppressWarnings("unchecked")
    @Override public void update(Subscribable<? extends GameEvent> topic, GameEvent message) {
	if(topic instanceof GameComponent) {
	    if(!((GameComponent)topic).alive())
		remove((T)topic);
	}
    }
    
}