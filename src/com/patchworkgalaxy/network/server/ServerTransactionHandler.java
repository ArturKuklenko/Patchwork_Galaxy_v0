package com.patchworkgalaxy.network.server;

import com.jme3.network.HostedConnection;
import com.patchworkgalaxy.network.server.account.Account;
import com.patchworkgalaxy.network.server.account.AccountManager;
import com.patchworkgalaxy.network.server.channel.Channel;
import com.patchworkgalaxy.network.server.channel.ChannelGame;
import com.patchworkgalaxy.network.server.channel.ChannelManager;
import com.patchworkgalaxy.network.transaction.NetTransaction;
import com.patchworkgalaxy.network.transaction.TransactionArgumentKey;
import com.patchworkgalaxy.network.transaction.TransactionException;

class ServerTransactionHandler {
    
    private final HostedConnection _connection;
    private final NetTransaction _transaction;
    
    ServerTransactionHandler(HostedConnection connection, NetTransaction transaction) {
	_connection = connection;
	_transaction = transaction;
    }
    
    Object handle() throws TransactionException {
	Account account = null;
	Channel channel = null;
	if(_transaction.getRequestType().isTargetsAccount()) {
	    account = getAccount();
	    if(account != null)
		account = checkPassword(account);
	    if(account != null)
		account = checkEmail(account);
	    if(account == null)
		throw new TransactionException(getAccountError());
	}
	if(_transaction.getRequestType().isTargetsChannel()) {
	    channel = ChannelManager.simple().getChannel(
		    _transaction.getArgument(TransactionArgumentKey.CHANNEL_NAME, true),
		    _transaction.getArgument(TransactionArgumentKey.CHANNEL_PASSWORD, true)
	    );
	}
	if(_transaction.getRequestType().isTargetsGame()) {
	    if(_transaction.hasArgument(TransactionArgumentKey.CHANNEL_NAME)) {
		channel = ChannelManager.games().getChannel(
			_transaction.getArgument(TransactionArgumentKey.CHANNEL_NAME, true),
			_transaction.getArgument(TransactionArgumentKey.CHANNEL_PASSWORD, true)
		);
	    }
	    else if(account != null)
		channel = account.getChannel();
	    if(!(channel instanceof ChannelGame)) {
		throw new TransactionException("You're not in a game");
	    }
	}
	return TransactionHandlers.handle(_connection, _transaction, account, channel);
    }
    
    private Account getAccount() {
	Account account;
	if(_transaction.hasArgument(TransactionArgumentKey.USERNAME))
	    account = AccountManager.getAccount(_transaction.getArgument(TransactionArgumentKey.USERNAME));
	else
	    account = AccountManager.getAccount(_connection);
	return account;
    }
    
    private Account checkPassword(Account account) {
	if(AccountManager.isLocalBehavior())
	    return account;
	if(account == null || !_transaction.hasArgument(TransactionArgumentKey.PASSWORD))
	    return account;
	if(account.checkPassword(_transaction.getArgument(TransactionArgumentKey.PASSWORD)))
	    return account;
	else
	    return null;
    }
    
    private Account checkEmail(Account account) {
	if(AccountManager.isLocalBehavior())
	    return account;
	if(account == null || !_transaction.hasArgument(TransactionArgumentKey.EMAIL))
	    return account;
	if(account.checkEmail(_transaction.getArgument(TransactionArgumentKey.EMAIL)))
	    return account;
	else
	    return null;
    }
    
    private String getAccountError() {
	if(!_transaction.hasArgument(TransactionArgumentKey.USERNAME))
	    return "You are not logged in";
	else if(_transaction.hasArgument(TransactionArgumentKey.PASSWORD))
	    return "Unknown username/password combination";
	else if(_transaction.hasArgument(TransactionArgumentKey.EMAIL))
	    return "Unknown username/email address combination";
	else if(_transaction.getArgument(TransactionArgumentKey.USERNAME).length() < 3)
	    return "That username is too short";
	else
	    return "That user is not logged in";
    }
    
}
