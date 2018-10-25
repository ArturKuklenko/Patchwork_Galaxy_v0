package com.patchworkgalaxy.display.oldui.ux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractUX implements UX {
    
    private final Map<Class<?>, Map<String, UXChannel<?>>> _channels;
    private final List<UXChannel<?>> _channelList;
    
    private UXMutex _mutex;
    
    public AbstractUX() {
	_channels = new HashMap<>();
	_channelList = new ArrayList<>();
    }
    
    @SuppressWarnings("unchecked")
    protected final <T> UXChannel<T> implyChannel(Class<T> type, String key, T defaultValue) {
	Map<String, UXChannel<?>> map = _channels.get(type);
	if(map == null) {
	    map = new HashMap<>();
	    _channels.put(type, map);
	}
	UXChannel<?> channel = map.get(key);
	if(channel == null) {
	    channel = createChannel(defaultValue);
	    map.put(key, channel);
	}
	return (UXChannel<T>)channel;
    }
    
    private <T> UXChannel<T> createChannel(T value) {
	UXChannel<T> channel = new UXChannel<>(value);
	_channelList.add(channel);
	return channel;
    }
    
    @Override
    public void update(float tpf) {
	for(int i = 0; i < _channelList.size(); ++i) {
	    _channelList.get(i).update(tpf);
	}
    }
    
    @Override
    public UXMutex acquireMutex() {
	if(_mutex == null) {
	    _mutex = new UXMutex(this);
	    for(UXChannel channel : _channelList) {
		channel.setMutex(_mutex);
	    }
	    return _mutex;
	}
	else
	    return null;
    }
    
    @Override
    public void releaseMutex(UXMutex mutex) {
	if(mutex != null && mutex == _mutex) {
	    _mutex = null;
	    mutex.release();
	}
    }
    
}
