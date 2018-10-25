package com.patchworkgalaxy.network.server.channel;

import com.patchworkgalaxy.network.transaction.TransactionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChannelManager<T extends Channel> {    
    
    private static ChannelManager<ChannelSimple> _simple;
    private static ChannelManager<ChannelGame> _games;
    
    private final Map<String, T> _channels;
    private final ChannelType _type;
    
    public static final String DEFAULT_CHANNEL_NAME = "Main Lobby";
    
    private ChannelManager(ChannelType type) {
	_channels = new HashMap<>();
	_type = type;
    }
    
    public static ChannelManager games() {
	if(_games == null) {
	    _games = new ChannelManager<>(ChannelType.GAME);
	    _games.initUpdater();
	}
	return _games;
    }
    
    public static ChannelManager simple() {
	if(_simple == null) {
	    _simple = new ChannelManager<>(ChannelType.SIMPLE);
	    _simple.initUpdater();
	}
	return _simple;
    }
    
    public static ChannelManager getInstance(ChannelType type) {
	return (type == ChannelType.SIMPLE) ? simple() : games();
    }
    
    public static Channel getDefaultChannel() throws TransactionException {
	return getInstance(ChannelType.SIMPLE).getOrCreateChannel(DEFAULT_CHANNEL_NAME, "");
    }
    
    public Channel getChannel(String name, String password) throws TransactionException {
	if(!_channels.containsKey(name)) {
	    if(name.equalsIgnoreCase(DEFAULT_CHANNEL_NAME)) {
		return getOrCreateChannel(DEFAULT_CHANNEL_NAME, password);
	    }
	    throw new TransactionException("No " + _type + " with that name exists");
	}
	Channel channel = _channels.get(name);
	channel.checkPassword(password);
	return channel;
    }
    
    @SuppressWarnings("unchecked")
    private T createChannel0(String name, String password) {
	if(_type == ChannelType.GAME)
	    return (T)new ChannelGame(name, password);
	else
	    return (T)new ChannelSimple(name, password);
    }
    
    public T createChannel(String name, String password) throws TransactionException {
	if(_channels.containsKey(name))
	    throw new TransactionException("A " + _type + " named " + name + " already exists");
	T channel = createChannel0(name, password);
	_channels.put(name, channel);
	return channel;
    }
    
    public Channel getOrCreateChannel(String name, String password) throws TransactionException {
	if(!_channels.containsKey(name))
	    return createChannel(name, password);
	else
	    return getChannel(name, password);
    }
    
    public Channel getChannelFromToken(ChannelToken token) throws TransactionException {
	if(token == null)
	    return getOrCreateChannel(DEFAULT_CHANNEL_NAME, "");
	try {
	    return getOrCreateChannel(token.getName(), token.getPassword());
	}
	catch(TransactionException e) {
	    return getOrCreateChannel(DEFAULT_CHANNEL_NAME, "");
	}
    }
    
    private void updateChannels() {
	Iterator<T> i = _channels.values().iterator();
	while(i.hasNext()) {
	    Channel channel = i.next();
	    if(channel.isEmpty())
		i.remove();
	    else
		channel.flushDelta();
	}
    }
    
    /**
     * @deprecated transitional code
     */
    @Deprecated public String describeChannels() {
	String result = "";
	for(Channel channel : _channels.values()) {
	    if(channel.isVisible())
		result += channel.getDescription() + "\n";
	}
	return result.trim();
    }
    
    private void initUpdater() {
	Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
	    new Runnable() {
		@Override
		public void run() {
		    updateChannels();
		}
	    },
	    1, 1, TimeUnit.SECONDS);
    }
    
}
