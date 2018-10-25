package com.patchworkgalaxy.network.server.account;

import com.jme3.network.HostedConnection;
import com.patchworkgalaxy.network.server.channel.ChannelManager;
import com.patchworkgalaxy.network.transaction.TransactionException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountManager {
    
    private final HashMap<String, Account> _accounts;
    private final HashMap<String, Account> _registration;
    private final HashMap<HostedConnection, Account> _logins;
    
    private static AccountManager _instance;
    
    private static boolean _isLocalServer;
    
    private AccountManager(List<Account> accounts) {
	_accounts = new HashMap<>();
	_registration = new HashMap<>();
	_logins = new HashMap<>();
	for(Account account : accounts) {
	    _accounts.put(account.getUsername(), account);
	}
    }
    
    @SuppressWarnings("unchecked")
    public static AccountManager getInstance() {
	if(_instance == null) {
	    try {
		_instance = new AccountManager(readAccountFiles());
	    } catch(IOException e) {
		e.printStackTrace();
		System.exit(1);
	    }
	}
	return _instance;
    }
    
    private static List<Account> readAccountFiles() throws IOException {
	File[] files = new File("./accounts").listFiles();
	if(files == null) return new ArrayList<>();
	List<Account> result = new ArrayList<>();
	for(File file : files)
	    result.add(Account.fromFile(file));
	return result;
    }
    
    public static String registerAccount(String username, String password, String email) {
	if(getInstance()._accounts.get(username) != null)
	    return "An account with that name already exists";
	String token = Account.securityToken();
	Account account = new Account(username, password, email);
	_instance._registration.put(token, account);
	account.writeFile();
	try {
	    AccountEmailer.sendEmail(email, "Patchwork Galaxy account registration",
		"Welcome to Patchwork Galaxy! An account was recently registered at this address."
		    + "\n\n"
		    + "Your account is " + username + ". For security reasons, it is currently disabled. To enable it, enter the following security token when prompted:"
		    + "\n" + token
		    + "\n\n"
		    + "Happy warmongering!"
		    + "\n\t~The Patchwork Galaxy team"
		    + "\n\n\n\n"
		    + "(If you did not register this account, ignore this message, or contact patrick@hailstormstudios.net.)"
	    );
	}
	catch(Exception e) {
	    return "That email address is invalid";
	}
	return null;
    }
    
    public static Account finishRegistration(HostedConnection connection, String token) {
	Account account = getInstance()._registration.get(token);
	if(account == null)
	    return null;
	_instance._registration.remove(token);
	_instance._accounts.put(account.getUsername(), account);
	account.setConnection(connection);
	_instance._logins.put(connection, account);
	return account;
    }
    
    public static Account getAccount(HostedConnection connection) {
	return getInstance()._logins.get(connection);
    }
    
    public static Account getAccount(String username) {
	Account result = getInstance()._accounts.get(username);
	if(result == null && ("Operator".equals(username) || _isLocalServer)) {
	    result = new Account(username, "", "");
	    _instance._accounts.put(username, result);
	    if(!_isLocalServer)
		result.writeFile();
	}
	return result;
    }
    
    public static void endConnection(HostedConnection connection) {
	endConnection(connection, "");
    }
    
    public static void endConnection(HostedConnection connection, String reason) {
	if(connection == null) return;
	Account account = getInstance()._logins.get(connection);
	if(account != null)
	    account.logout();
	getInstance()._logins.remove(connection);
	connection.close(reason);
    }
    
    public static boolean login(HostedConnection connection, String username, String password) throws TransactionException {
	Account account = getInstance()._accounts.get(username);
	if(account == null || !account.checkPassword(password))
	    throw new TransactionException("Unknown username/password combination");
	HostedConnection oldConnection = account.getConnection();
	if(oldConnection != null)
	    endConnection(oldConnection, "Logged in from another client");
	_instance._logins.put(connection, account);
	account.setConnection(connection);
	account.setChannel(ChannelManager.getDefaultChannel());
	return true;
    }
    
    public static void enableLocalBehavior() {
	_isLocalServer = true;
    }
    
    public static boolean isLocalBehavior() {
	return _isLocalServer;
    }
    
}
