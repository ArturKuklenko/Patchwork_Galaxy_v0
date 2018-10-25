package com.patchworkgalaxy.display.oldui.ux;

import java.util.ArrayList;
import java.util.List;

public class UXMutex extends ChannelMutex {
    
    private final UX _ux;
    private final List<UXChannel<?>> _channels;
    
    UXMutex(UX ux) {
	super(null);
	_ux = ux;
	_channels = new ArrayList<>();
    }
    
    void add(UXChannel<?> channel) {
	_channels.add(channel);
    }
    
    @Override
    public void release() {
	for(UXChannel<?> channel : _channels)
	    channel.releaseMutex(this);
	_ux.releaseMutex(this);
    }
    
}
