package com.patchworkgalaxy.network.server.channel;

import com.jme3.network.Message;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.game.state.evolution.EvolutionBuilder;
import com.patchworkgalaxy.game.state.evolution.Opcode;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.network.server.account.Account;
import com.patchworkgalaxy.network.transaction.TransactionException;
import com.patchworkgalaxy.udat.ChannelData;
import com.patchworkgalaxy.udat.SpecialKeys;
import com.patchworkgalaxy.udat.UDatManager;
import java.util.ArrayList;
import java.util.List;

public class ChannelGame extends Channel {
    
    private final ServerGame _game;
    private final List<Account> _players;
    private String _host;
    
    private final int MAX_PLAYERS = 2;
    
    ChannelGame(String name, String password) {
	super(name, password);
	_game = new ServerGame();
	_players = new ArrayList<>();
    }

    public void start() throws TransactionException {
	if(_game.started())
	    throw new TransactionException("That game has already started");
	if(_players.size() != MAX_PLAYERS)
	    throw new TransactionException("You can't start a game without an opponent");
	for(Account account : _players) {
	    if(account != _players.get(0) && !account.booleanUserDatum(SpecialKeys.READY))
		throw new TransactionException(account.getUsername() + " is not ready");
	}
	GameHistory history = _game.start(_players);
	for(int i = 0; i < _players.size(); ++i)
	    _players.get(i).receiveMessage(history.withLocalId(i));
	
	transmitExcluding(history.withLocalId(-1), _players);
	
    }
    
    @Override
    public boolean isVisible() {
	return !isPasswordProtected() && _players.size() < MAX_PLAYERS;
    }

    @Override
    void onJoinAttempt(Account account) throws TransactionException {
	if(_game.started())
	    throw new TransactionException("That game has already started");
	if(_players.size() >= MAX_PLAYERS) {
	    if(!account.booleanUserDatum(SpecialKeys.OBSERVER))
		throw new TransactionException("That game is full");
	}
	account.onJoinedGame();
	if(_players.isEmpty()) {
	    _host = account.getUsername();
	    account.setUserDatum(SpecialKeys.HOST, "true", null);
	}
	else {
	    account.setUserDatum(SpecialKeys.HOST, "false", null);
	    account.setUserDatum(SpecialKeys.READY, "false", null);
	}
	_players.add(account);
	ChatMessage logthat = new ChatMessage("", account.getUsername() + " joined this game", "aaaaaa");
	logthat.suppressColon();
	transmitToAll(logthat);
    }

    @Override
    void onLeft(Account account, String reason) {
	if(reason == null || reason.length() == 0) {
	    if(_game.started())
		reason = " conceded";
	    else
		reason = " left the game";
	}
	_players.remove(account);
	ChatMessage logthat = new ChatMessage("", account.getUsername() + reason, "aaaaaa");
	logthat.suppressColon();
	transmitToAll(logthat);
	if(_game.started()) {
	    try {
		Evolution concede = new EvolutionBuilder(Opcode.CONCEDE, _game.getPlayer(account).getId()).getEvolution();
		transmitToAll(concede);
		UDatManager.getInstance().onLeaveGame(account.getUserData());
		account.onLeaveGame();
	    } catch(NullPointerException e) {} //not entirely sure why this happens...
	}
    }

    @Override
    void onMessageReceived(Message message) {
	if(message instanceof ChatMessage)
	    transmitToAll(message);
	if(message instanceof Evolution) {
	    if(_game.tryEvolution((Evolution)message))
		transmitToAll(message);
	}
    }
    
    @Override
    public ChannelToken getToken() {
	return null;
    }
    
    @Override
    ChannelData pretransmit(ChannelData message) {
	message.setHostUsername(_host);
	return message;
    }
    
    @Override
    void onUserDataChanged(Account account, String key, String value) throws TransactionException {
	if(SpecialKeys.OBSERVER.equals(key)) {
	    /*if(account.getUsername().equalsIgnoreCase(_host)) {
		throw new TransactionException("You can't observe a game you're hosting.");
	    }
	    else*/ if(_players.contains(account)) {
		_players.remove(account);
		chatlog(account, "is just observing.");
	    }
	    else {
		if(_players.size() == MAX_PLAYERS) {
		    throw new TransactionException("The game is full.");
		}
		_players.add(account);
		chatlog(account, "is no longer observing.");
	    }
	}
	if(SpecialKeys.READY.equals(key)) {
	    if(Boolean.valueOf(value))
		chatlog(account, "is ready!");
	    else
		chatlog(account, "is not ready");
	}
    }
    
}
