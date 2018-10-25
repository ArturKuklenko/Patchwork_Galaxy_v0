package com.patchworkgalaxy.general.data;

import java.util.List;

public class MutableGameProps extends GameProps {
    
    public MutableGameProps() {
	super();
    }
    
    public MutableGameProps(GameProps copyOf) {
	super(copyOf);
    }
    
    @Override
    public GameProps mutable() {
	return this;
    }
    
    @Override
    public GameProps immutable() {
	return new GameProps(this);
    }
    
    @Override
    public GameProps set(String key, Object value) {
	contents.put(key, value);
	return this;
    }
    
    @Override
    <T> List<T> convList(List<T> list) {
	return list;
    }
    
    public <T> MutableGameProps appendToList(Class<T> type, String key, T value) {
	List<T> list = implyList(type, key);
	if(list != null)
	    list.add(value);
	return this;
    }
    
}
