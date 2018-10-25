package com.patchworkgalaxy.udat;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Serializable
public final class UserData extends AbstractMessage implements java.io.Serializable {
    
    private boolean _delta;
    private String _username;
    private Map<String, String> _data;
    
    /**
     * @deprecated Serialization only.
     */
    @Deprecated
    public UserData() {
	this("", false);
    }
    
    /**
     * Constructs a non-{@linkplain #isDelta() delta} userdata with the same
     * properties and username as another.
     * @param other 
     */
    public UserData(UserData other) {
	this(other.getUsername(), false);
	_data.putAll(other.getData());
	setReliable(true);
    }
    
    public UserData(String username, boolean delta) {
	_username = username == null ? "" : username;
	_delta = delta;
	_data = new HashMap<>();
	setReliable(true);
    }
    
    public UserData(String username, boolean delta, Map<String, String> data) {
	_username = username == null ? "" : username;
	_delta = delta;
	_data = data == null ? new HashMap<String, String>() : data;
	setReliable(true);
    }
    
    public boolean isDelta() {
	return _delta;
    }
    
    public String getUsername() {
	return _username;
    }
    
    public Map<String, String> getData() {
	return new HashMap<>(_data);
    }
    
    public String getDatum(String key) {
	return _data.containsKey(key) ? _data.get(key) : "";
    }
    
    public boolean booleanDatum(String key) {
	return Boolean.valueOf(getDatum(key));
    }
    
    void setDatum(String key, String value) {
	_data.put(key, value);
    }
    
    public void update(UserData other) {
	if(!_username.equals(other._username))
	    throw new IllegalArgumentException("Tried to update userdata with the wrong name");
	if(other.isDelta()) {
	    for(Entry<String, String> i: other._data.entrySet()) {
		String key = i.getKey();
		String value = i.getValue();
		if(value == null)
		    _data.remove(key);
		else
		    _data.put(key, value);
	    }
	}
	else
	    _data = other.getData();
    }
    
    @Override
    public String toString() {
	StringBuilder result = new StringBuilder();
	if(_delta)
	    result.append("Delta:");
	result.append("User ").append(_username);
	for(Entry<String, String> e : _data.entrySet()) {
	    result.append("\n\t")
		    .append(e.getKey())
		    .append(" -> ")
		    .append(e.getValue());
	}
	return result.toString();
    }
    
}
