package com.patchworkgalaxy.client;

import com.jme3.network.Client;
import com.jme3.network.Network;
import com.patchworkgalaxy.Effort;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.udat.ChannelData;
import java.io.IOException;

public class ClientManager {
    
    private ClientManager() {}
    
    static PWGClient _currentClient;
    
    public static PWGClient client() {
	return _currentClient;
    }

    public static Effort connect(String hostname, String port) {
	return Efforts.submit(new ConnectionEffort(hostname, port));
    }
    
    public static boolean isConnected() {
	return _currentClient != null && _currentClient.isConnected();
    }

    static PWGClient createClient(String hostname, int port) throws IOException {
	if (_currentClient != null)
	    _currentClient.disconnect();
	Client conn = Network.connectToServer(hostname, port);
	PWGClient pgc = new PWGClient(conn);
	conn.addMessageListener(pgc, ProtocolMessage.class);
	conn.addMessageListener(pgc, Evolution.class);
	conn.addMessageListener(pgc, GameHistory.class);
	conn.addMessageListener(pgc, ChatMessage.class);
	conn.addMessageListener(pgc, ChannelData.class);
	conn.addClientStateListener(new PWGClientStateListener());
	conn.addErrorListener(pgc);
	conn.start();
	_currentClient = pgc;
	return pgc;
    }
    
}
