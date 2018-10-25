package com.patchworkgalaxy.game.vital;

import java.util.HashMap;
import java.util.Map;

public class VitalStore {
    
    private final Map<String, Vital> _vitals;
    
    public VitalStore() {
	_vitals = new HashMap<>();
    }
    
    public Vital addVital(String key, int max) {
	
	if(_vitals.containsKey(key))
	    throw new IllegalArgumentException("Already have vital at key " + key);
	
	if(max < 0)
	    throw new IllegalArgumentException("Can't create vital with negative maximum");
	    
	Vital vital = new Vital(max);
	_vitals.put(key, vital);
	return vital;
	
    }
    
    public Vital addVital(String key) {
	
	if(_vitals.containsKey(key))
	    throw new IllegalArgumentException("Already have vital at key " + key);
	    
	Vital vital = Vital.unlimited();
	_vitals.put(key, vital);
	return vital;
	
    }
    
    public Vital getVital(String key) {
	Vital result = _vitals.get(key);
	return result;
    }
    
    public boolean hasVital(String key) {
	return _vitals.containsKey(key);
    }
    
    public Vital stackVitals(String key, String... stack) {
	
	if(_vitals.containsKey(key))
	    throw new IllegalArgumentException("Already have vital at key " + key);
	
	if(stack.length <= 0)
	    throw new IllegalArgumentException("Vital stack needs one or more elements");
	
	Vital[] vstack = new Vital[stack.length];
	
	for(int i = stack.length; --i >= 0;) {
	    
	    String s = stack[i];
	    Vital v = getVital(s);
	    if(v == null)
		throw new IllegalArgumentException("Stack element " + s + " doesn't correspond to a known vital");
	    else
		vstack[i] = v;
	    
	}
	
	Vital result = new VitalStack(true, vstack);
	_vitals.put(key, result);
	return result;
	
    }
    
}
