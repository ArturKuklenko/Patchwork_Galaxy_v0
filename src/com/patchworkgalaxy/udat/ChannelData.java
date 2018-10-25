package com.patchworkgalaxy.udat;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Serializable
public final class ChannelData extends AbstractMessage {
    
    private boolean _delta;
    private String _channelName;
    private String _host;
    private Map<String, UserData> _users;
    private List<String> _remove;
    
    /**
     * @deprecated Serialization only.
     */
    @Deprecated
    public ChannelData() {
	this(false);
    }
    
    public ChannelData(boolean delta) {
	this("", delta);
    }
    
    public ChannelData(String channelName, boolean delta) {
	_channelName = channelName == null ? "" : channelName;
	_delta = delta;
	_users = new HashMap<>();
	_remove = new ArrayList<>();
    }
    
    /**
     * Constructs a copy of another channeldata.
     * @param copyOf 
     */
    public ChannelData(ChannelData copyOf) {
	_channelName = copyOf._channelName;
	_delta = copyOf._delta;
	_users = new HashMap<>();
	_users.putAll(copyOf._users);
	_host = copyOf._host;
    }
    
    public boolean isDelta() {
	return _delta;
    }
    
    public String getChannelName() {
	return _channelName;
    }
    
    public void add(UserData userdata) {
	_users.put(userdata.getUsername(), userdata);
    }
    
    public void remove(String username) {
	if(isDelta())
	    _remove.add(username);
	_users.remove(username);
    }
    
    Map<String, UserData> getUserdata() {
	return new HashMap<>(_users);
    }
    
    public void update(ChannelData other) {
	if(other._channelName.length() > 0)
	    _channelName = other._channelName;
	if(other.isDelta()) {
	    for(Entry<String, UserData> i : other.getUserdata().entrySet()) {
		String key = i.getKey();
		UserData value = i.getValue();
		if(value != null && !_users.containsKey(key))
		    add(new UserData(value));
		else
		    _users.get(key).update(value);
	    }
	    for(String remove : other._remove)
		_users.remove(remove);
	}
	else {
	    _users = other.getUserdata();
	}
	_host = other._host;
    }
    
    public Collection<String> getUsernames() {
	return _users.keySet();
    }
    
    public Set<UserData> getUserDatas() {
	return new HashSet<>(_users.values());
    }
    
    public UserData getUserData(String username) {
	UserData data = _users.get(username);
	if(data == null) return null;
	return new UserData(data);
    }
    
    public void update(UserData userdata) {
	String key = userdata.getUsername();
	if(_users.containsKey(key))
	    _users.get(key).update(userdata);
	else
	    add(new UserData(userdata));
    }
    
    public boolean isEmpty() {
	return _users.isEmpty() && _remove.isEmpty();
    }
    
    public String getHostUsername() {
	return _host;
    }
    
    public void setHostUsername(String hostUsername) {
	_host = hostUsername;
    }
    
    @Override
    public String toString() {
	String result = "Channel data:";
	if(_delta)
	    result += "delta-";
	result += _channelName;
	result += " " + _host;
	result += "\n\tusers:";
	for(String username : _users.keySet())
	    result += username + ", ";
	result += "\n\tremove:";
	for(String username : _remove)
	    result += username;
	return result;
    }
    
    public String getDatum(String username, String key) {
	UserData data = _users.get(username);
	if(data == null)
	    return null;
	return data.getDatum(key);
    }
    
}