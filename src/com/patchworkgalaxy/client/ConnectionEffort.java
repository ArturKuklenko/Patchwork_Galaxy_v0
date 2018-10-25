package com.patchworkgalaxy.client;

import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.general.util.TopicListenerEffort;

class ConnectionEffort extends TopicListenerEffort {
    
    private final String _hostname;
    private final String _port;
    
    ConnectionEffort(String hostname, String port) {
	super(Topic.CLIENT_CONNECTED);
	_hostname = hostname;
	_port = port;
    }
    
    @Override public PWGClient call() throws Exception {
	if(ClientManager.isConnected()) return ClientManager.client();
	ClientManager.createClient(_hostname, Integer.valueOf(_port));
	return ClientManager.client();
    }
    
}
