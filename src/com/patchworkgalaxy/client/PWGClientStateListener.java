package com.patchworkgalaxy.client;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.patchworkgalaxy.general.subscriptions.Topic;

class PWGClientStateListener implements ClientStateListener {

    @Override public void clientConnected(Client c) {
	Topic.CLIENT_CONNECTED.update();
    }

    @Override public void clientDisconnected(Client c, DisconnectInfo info) {
	Topic.CLIENT_DISCONNECTED.update(info);
    }
    
}
