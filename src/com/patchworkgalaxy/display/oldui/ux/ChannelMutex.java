package com.patchworkgalaxy.display.oldui.ux;

public class ChannelMutex {
    
    private final UXChannel<?> _channel;
    
    ChannelMutex(UXChannel<?> channel) {
	_channel = channel;
    }
    
    public void release() {
	_channel.releaseMutex(this);
    }
    
    @Override
    protected void finalize() throws Throwable {
	super.finalize();
	release();
    }
    
}
