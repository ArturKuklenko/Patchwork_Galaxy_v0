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
import static com.patchworkgalaxy.network.transaction.TransactionType.CREATE_CHANNEL;
import static com.patchworkgalaxy.network.transaction.TransactionType.JOIN_CHANNEL;
import static com.patchworkgalaxy.network.transaction.TransactionType.JOIN_GAME;

class TransactionHandlers {
    
    private TransactionHandlers() {}
    
    private static String GENERIC_SUCCESS = "";
    
    static Object handle(HostedConnection connection, NetTransaction transaction, Account account, Channel channel) throws TransactionException {
	switch(transaction.getRequestType()) {
	case LOGIN:
	    return login(
		    connection,
		    transaction.getArgument(TransactionArgumentKey.USERNAME, true),
		    transaction.getArgument(TransactionArgumentKey.PASSWORD, false)
		    );
	case BEGIN_REGISTRATION:
	    return beginRegistration(
		    transaction.getArgument(TransactionArgumentKey.REGISTER_NAME, true),
		    transaction.getArgument(TransactionArgumentKey.PASSWORD, true),
		    transaction.getArgument(TransactionArgumentKey.EMAIL, true)
		    );
	case FINISH_REGISTRATION:
	    return finishRegistration(
		    connection,
		    transaction.getArgument(TransactionArgumentKey.SECURITY_TOKEN, true)
		    );
	case RESET_PASSWORD:
	    return resetPassword(
		    account,
		    transaction.getArgument(TransactionArgumentKey.EMAIL, true)
		    );
	case REQUEST_PASSWORD_CHANGE:
	    return requestPasswordChange(
		    account,
		    transaction.getArgument(TransactionArgumentKey.PASSWORD, false),
		    transaction.getArgument(TransactionArgumentKey.CHANGE_PASSWORD_TO, true)
		    );
	case REQUEST_EMAIL_CHANGE:
	    return requestEmailChange(
		    account,
		    transaction.getArgument(TransactionArgumentKey.PASSWORD, false),
		    transaction.getArgument(TransactionArgumentKey.CHANGE_EMAIL_TO, true)
		    );
	case USE_SECURITY_TOKEN:
	    return useSecurityToken(
		    account,
		    transaction.getArgument(TransactionArgumentKey.SECURITY_TOKEN, true)
		    );
	case JOIN_CHANNEL:
	    return joinChannel(account, channel);
	case CREATE_CHANNEL:
	    return joinNewChannel(
		    account,
		    transaction.getArgument(TransactionArgumentKey.CHANNEL_NAME, true),
		    transaction.getArgument(TransactionArgumentKey.CHANNEL_PASSWORD, true)
	    );
	case JOIN_GAME:
	    return joinGame(account, channel);
	case CREATE_GAME:
	    return joinNewGame(
		    account,
		    transaction.getArgument(TransactionArgumentKey.CHANNEL_NAME, true),
		    transaction.getArgument(TransactionArgumentKey.CHANNEL_PASSWORD, true)
	    );
	case JOIN_PREVIOUS_CHANNEL:
	    return joinPreviousChannel(account);
	case LIST_PUBLIC_CHANNELS:
	    return listPublicChannels();
	case LIST_PUBLIC_GAMES:
	    return listPublicGames();
	case START_GAME:
	    return startGame((ChannelGame)channel);
	case SET_USER_DATA:
	    return setUserData(
		    account,
		    transaction.getArgument(TransactionArgumentKey.UDAT_KEY, true),
		    transaction.getArgument(TransactionArgumentKey.UDAT_VALUE, true)
		    );
	case DESCRIBE_USER:
	    return describeUser(account);
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    private static Object login(HostedConnection connection, String username, String password) throws TransactionException {
	if(AccountManager.login(connection, username, password))
	    return null;
	throw new TransactionException("Invalid login");
    }

    private static Object beginRegistration(String username, String password, String email) throws TransactionException {
	String error = AccountManager.registerAccount(username, password, email);
	if(error != null)
	    throw new TransactionException(error);
	return GENERIC_SUCCESS;
    }
    
    private static Object finishRegistration(HostedConnection connection, String securityToken) throws TransactionException {
	if(AccountManager.finishRegistration(connection, securityToken) == null)
	    throw new TransactionException("Incorrect token - check your email account for the correct token");
	else
	    return GENERIC_SUCCESS;
    }

    private static Object resetPassword(Account account, String email) throws TransactionException {
	String error = account.requestPasswordReset(email);
	if(error != null)
	    throw new TransactionException(error);
	return GENERIC_SUCCESS;
    }

    private static Object requestPasswordChange(Account account, String oldPassword, String newPassword) throws TransactionException {
	String error = account.requestPasswordChange(oldPassword, newPassword);
	if(error != null)
	    throw new TransactionException(error);
	return GENERIC_SUCCESS;
    }

    private static Object requestEmailChange(Account account, String password, String email) throws TransactionException {
	String error = account.requestEmailChange(password, email);
	if(error != null)
	    throw new TransactionException(error);
	return GENERIC_SUCCESS;
    }

    private static Object useSecurityToken(Account account, String securityToken) throws TransactionException {
	String error = account.acceptSecurityToken(securityToken);
	if(error != null)
	    throw new TransactionException(error);
	else
	    return GENERIC_SUCCESS;
    }

    private static Object joinChannel(Account account, Channel channel) throws TransactionException {
	account.setChannel(channel);
	return GENERIC_SUCCESS;
    }

    private static Object joinGame(Account account, Channel channel) throws TransactionException {
	account.setChannel(channel);
	return GENERIC_SUCCESS;
    }

    private static Object joinNewChannel(Account account, String channelName, String channelPassword) throws TransactionException {
	Channel channel = ChannelManager.simple().createChannel(channelName, channelPassword);
	if(channel == null)
	    throw new TransactionException("Unknown error creating channel");
	account.setChannel(channel);
	return GENERIC_SUCCESS;
    }

    private static Object joinNewGame(Account account, String channelName, String channelPassword) throws TransactionException {
	Channel channel = ChannelManager.games().createChannel(channelName, channelPassword);
	if(channel == null)
	    throw new TransactionException("Unknown error hosting game");
	account.setChannel(channel);
	return GENERIC_SUCCESS;
    }

    private static Object joinPreviousChannel(Account account) throws TransactionException {
	account.popChannel();
	return GENERIC_SUCCESS;
    }

    private static Object listPublicChannels() throws TransactionException {
	return ChannelManager.simple().describeChannels();
    }

    private static Object listPublicGames() throws TransactionException {
	return ChannelManager.games().describeChannels();
    }
    
    private static Object startGame(ChannelGame channel) throws TransactionException {
	channel.start();
	return GENERIC_SUCCESS;
    }

    private static Object setUserData(Account account, String key, String value) throws TransactionException {
	account.setUserDatum(key, value, account);
	return GENERIC_SUCCESS;
    }

    private static Object describeUser(Account account) throws TransactionException {
	return account.getUserData();
    }
    
}
