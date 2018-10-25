package com.patchworkgalaxy.client;

import com.jme3.math.ColorRGBA;
import com.jme3.network.Client;
import com.jme3.network.ErrorListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.patchworkgalaxy.Effort;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.uidefs.ChatChannelAux;
import com.patchworkgalaxy.display.oldui.uidefs.Notifier;
import com.patchworkgalaxy.game.state.ClientGameState;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.game.state.evolution.EvolutionException;
import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.network.oldmessages.ProtocolCallbacks;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.network.transaction.NetTransaction;
import com.patchworkgalaxy.network.transaction.Response;
import com.patchworkgalaxy.network.transaction.TransactionType;
import com.patchworkgalaxy.udat.ChannelData;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.Callable;

public class PWGClient extends Observable implements MessageListener<Client>, ErrorListener<Client> {
    
    private final Client _connection;
    private final List<Message> _incomingMessageQueue, _outgoingMessageQueue;
    private int _nextTransactionId;
    private final ChannelData _channelData;
    
    private final Transitional _transitional;
    
    PWGClient(Client client) {
	_incomingMessageQueue = new ArrayList<>();
	_outgoingMessageQueue = new ArrayList<>();
	_connection = client;
	_channelData = new ChannelData(false);
	_transitional = new Transitional(this);
    }
    
    public static void tick() {
	if(ClientManager.client() != null)
	    ClientManager.client().processMessageQueues();
    }
    
    public void send(Message m) {
	_outgoingMessageQueue.add(m);
    }
    
    public void sendChatMessage(String message) {
	send(new ChatMessage(message));
    }
    
    public Effort transaction(TransactionType type, String... args) {
	NetTransaction transaction = new NetTransaction(++_nextTransactionId, type, args);
	OutstandingTransaction outstanding = new OutstandingTransaction(this, transaction);
	_connection.addMessageListener(outstanding, Response.class);
	Effort result = Efforts.submit(outstanding);
	send(transaction);
	return result;
    }
    
    void concludeTransaction(OutstandingTransaction transaction) {
	_connection.removeMessageListener(transaction);
    }
    
    /**
     * @deprecated old-style messaging code
     */
    @Deprecated public PWGClient send(ProtocolMessage m, ProtocolCallbacks c) {
	_transitional.translate(m, c);
	return this;
    }
    
    public void disconnect() {
	try {
	    _connection.close();
	}
	catch(Exception e) {}
    }
    
    private void processMessageQueues() {
	if(!(_connection != null && _connection.isConnected())) {
	    if(!_incomingMessageQueue.isEmpty())
		_incomingMessageQueue.clear();
	    if(!_outgoingMessageQueue.isEmpty())
		_outgoingMessageQueue.clear();
	    return;
	}
	if(!_incomingMessageQueue.isEmpty()) {
	    Message message = _incomingMessageQueue.remove(0);
	    handleIncomingMessage(message);
	}
	if(!_outgoingMessageQueue.isEmpty()) {
	    Message message = _outgoingMessageQueue.remove(0);
	    _connection.send(message);
	}
    }
    
    @Override
    public void messageReceived(Client source, Message m) {
	//okay, I'll bite, what's up with these conditionals?
	if(_connection.equals(source) || m instanceof ChatMessage)
	    _incomingMessageQueue.add(m);
    }

    @Override
    public void handleError(Client source, Throwable t) {
	if(source.equals(ClientManager.client()._connection)) {
	    disconnect();
	    Notifier.notify(new ColoredText("Connection error\n\n").addText(t.getLocalizedMessage(), ColorRGBA.Red));
	}
    }

    private void handleIncomingMessage(Message message) {
	if (message instanceof GameHistory) {
	    GameAppState.startGame((GameHistory) message);
	} else if (message instanceof ChatMessage) {
	    handleChatMessage((ChatMessage) message);
	} else if (message instanceof Evolution) {
	    handleEvolutionMessage((Evolution) message);
	} else if (message instanceof ChannelData) {
	    handleChannelMessage((ChannelData) message);
	} else {
	    throw new AssertionError();
	}
    }

    private void handleChatMessage(ChatMessage message) {
	UX2D.getInstance().acceptChatInput(message.getColoredText());
    }

    private void handleChannelMessage(ChannelData message) {
	_channelData.update(message);
    }
    
    private void handleEvolutionMessage(final Evolution evolution) {
	PatchworkGalaxy.schedule(new Callable<Void>() {
	    @Override
	    public Void call() throws EvolutionException {
		ClientGameState.getCurrentInstance().evolve(evolution);
		return null;
	    }
	});
    }

    public void onGameStateEnded() {
	PatchworkGalaxy.stateManager().detach(GameAppState.getInstance());
	PatchworkGalaxy.rootNode().detachAllChildren();
	send(new ProtocolMessage("JBAK"), ChatChannelAux.JOINED_CHANNEL);
	Topic.GAME_ENDED.update();
    }
    
    public ChannelData getChannelData() {
	return new ChannelData(_channelData);
    }
    
    public void update() {
	setChanged();
	notifyObservers();
    }
    
    public boolean isConnected() {
	return _connection.isConnected();
    }
    
}
