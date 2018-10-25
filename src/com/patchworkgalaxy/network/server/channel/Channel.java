package com.patchworkgalaxy.network.server.channel;

import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.network.server.PatchworkGalaxyServer;
import com.patchworkgalaxy.network.server.account.Account;
import com.patchworkgalaxy.network.transaction.TransactionException;
import com.patchworkgalaxy.udat.ChannelData;
import com.patchworkgalaxy.udat.UserData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public abstract class Channel {
    
    private final List<Account> _accounts;
    private final String _name, _password;
    
    private ChannelData _channelData, _channelDelta;
    
    Channel(String name, String password) {
	if(name == null)
	    throw new IllegalArgumentException("Channel can't have null name");
	if(password == null)
	    throw new IllegalArgumentException("Channel can't have null password");
	if(ChannelManager.DEFAULT_CHANNEL_NAME.equals(name))
	    password = "";
	_name = name;
	_password = password;
	_accounts = new ArrayList<>();
	_channelData = new ChannelData(name, false);
	_channelDelta = new ChannelData(name, true);
    }
    
    public abstract boolean isVisible();
    abstract void onJoinAttempt(Account account) throws TransactionException;
    abstract void onLeft(Account account, String reason);
    abstract void onMessageReceived(Message message);
    abstract void onUserDataChanged(Account account, String key, String value) throws TransactionException;
    abstract ChannelData pretransmit(ChannelData message);
    
    public void addAccount(Account account) throws TransactionException {
	onJoinAttempt(account);
	updateUserdata(account, account.getUserData());
	flushDelta();
	transmitSpecificallyTo(_channelData, account);
	_accounts.add(account);
    }
    
    public void removeAccount(Account account, String reason) {
	if(_accounts.remove(account)) {
	    onLeft(account, reason);
	    _channelDelta.remove(account.getUsername());
	    flushDelta();
	}
    }
    
    public boolean isPasswordProtected() {
	return _password.length() > 0;
    }
    
    public void receive(Message m) {
	onMessageReceived(m);
    }
    
    public void transmitToAll(Message m) {
	transmit0(m, _accounts);
    }
    
    public void transmitExcluding(Message m, List<Account> exclude) {
	ArrayList<Account> accounts = new ArrayList<>(_accounts);
	accounts.removeAll(exclude);
	transmit0(m, accounts);
    }
    
    public void transmitExcluding(Message m, Account... exclude) {
	transmitExcluding(m, Arrays.asList(exclude));
    }
    
    public void transmitSpecificallyTo(Message m, List<Account> to) {
	transmit0(m, to);
    }
    
    public void transmitSpecificallyTo(Message m, Account... to) {
	transmit0(m, Arrays.asList(to));
    }
    
    private void transmit0(Message m, List<Account> accounts) {
	if(m instanceof ChannelData)
	    m = pretransmit((ChannelData)m);
	List<HostedConnection> connections = new ArrayList<>();
	for(Account account : accounts)
	    connections.add(account.getConnection());
	PatchworkGalaxyServer.getInstance().broadcast(Filters.in(connections), m);	
    }
    
    public void updateUserdata(Account account, UserData delta) throws TransactionException {
	if(_accounts.contains(account)) {
	    for(Entry<String, String> i : delta.getData().entrySet()) {
		String key = i.getKey();
		String val = i.getValue();
		String old = _channelData.getDatum(account.getUsername(), key);
		if(!val.equalsIgnoreCase(old))
		    onUserDataChanged(account, i.getKey(), i.getValue());
	    }
	}
	_channelDelta.update(delta);
    }
    
    public void flushDelta() {
	if(!_channelDelta.isEmpty()) {
	    _channelData.update(_channelDelta);
	    transmitToAll(_channelDelta);
	    _channelDelta = new ChannelData(_name, true);
	}
    }
    
    void checkPassword(String password) throws TransactionException {
	if(isPasswordProtected() && !_password.equals(password))
	    throw new TransactionException("Unknown channel/password combination");
    }
    
    Set<Account> getAccounts() {
	return new HashSet<>(_accounts);
    }
    
    public boolean isEmpty() {
	return _accounts.isEmpty();
    }
    
    public String getName() {
	return _name;
    }
    
    public ChannelToken getToken() {
	return new ChannelToken(_name, _password);
    }
    
    void chatlog(Account account, String message) {
	ChatMessage logthat = new ChatMessage(account.getUsername(), " " + message);
	logthat.suppressColon();
	transmitToAll(logthat);
    }
    
    /**
     * @deprecated transitional code
     * @return name;;public;;isGame;;size;;host
     */
    @Deprecated String getDescription() {
	String host = _accounts.isEmpty() ? null : _accounts.get(0).getUsername();
	return _name + ";;" + isVisible() + ";;" + getClass().getSimpleName() + ";;" + _accounts.size() + ";;" + host;
    }
    
}
