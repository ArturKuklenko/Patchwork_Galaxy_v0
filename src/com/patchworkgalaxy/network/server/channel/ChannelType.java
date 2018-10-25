package com.patchworkgalaxy.network.server.channel;

public enum ChannelType {
   
    SIMPLE {
	@Override
	public String toString() {
	    return "channel";
	}
    },
    
    GAME {
	@Override
	public String toString() {
	    return "game";
	}
    },
    
    ;
    
}
