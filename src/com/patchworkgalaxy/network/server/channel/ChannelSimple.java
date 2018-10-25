package com.patchworkgalaxy.network.server.channel;

import com.jme3.network.Message;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.network.server.account.Account;
import com.patchworkgalaxy.network.transaction.TransactionException;
import com.patchworkgalaxy.udat.ChannelData;

class ChannelSimple extends Channel {
    
    ChannelSimple(String name, String password) {
	super(name, password);
    }

    @Override
    public boolean isVisible() {
	return !isPasswordProtected();
    }

    @Override
    void onJoinAttempt(Account account) throws TransactionException {
	ChatMessage logthat = new ChatMessage("", account.getUsername() + " joined " + getName(), "aaaaaa");
	logthat.suppressColon();
	transmitToAll(logthat);
    }

    @Override
    void onLeft(Account account, String reason) {
	String detail = (reason == null || reason.length() == 0) ? " left " + getName() : reason;
	ChatMessage logthat = new ChatMessage("", account.getUsername() + detail, "aaaaaa");
	logthat.suppressColon();
	transmitToAll(logthat);
    }

    @Override
    void onMessageReceived(Message message) {
	if(message instanceof ChatMessage)
	    transmitToAll(message);
    }
    
    @Override
    ChannelData pretransmit(ChannelData message) {
	return message;
    }
    
    @Override void onUserDataChanged(Account account, String key, String value) {}
    
}
