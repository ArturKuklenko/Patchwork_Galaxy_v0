package com.patchworkgalaxy.general.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ListMap<K, V> extends LinkedHashMap<K, List<V>> {
    
    private static final long serialVersionUID = 1L;
    
    public void append(K key, V value) {
	imply(key).add(value);
    }
    
    private List<V> imply(K key) {
	List<V> list = get(key);
	if(list == null) {
	    list = new ArrayList<>();
	    put(key, list);
	}
	return list;
    }
    
}
