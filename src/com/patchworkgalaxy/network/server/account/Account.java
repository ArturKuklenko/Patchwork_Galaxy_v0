package com.patchworkgalaxy.network.server.account;

import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.network.server.PatchworkGalaxyServer;
import com.patchworkgalaxy.network.server.channel.Channel;
import com.patchworkgalaxy.network.server.channel.ChannelManager;
import com.patchworkgalaxy.network.server.channel.ChannelToken;
import com.patchworkgalaxy.network.transaction.TransactionException;
import com.patchworkgalaxy.udat.UDatException;
import com.patchworkgalaxy.udat.UDatManager;
import com.patchworkgalaxy.udat.UserData;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.crackstation.PasswordHash;

public class Account implements Serializable {
    
    private static final PatchworkGalaxyServer _server = PatchworkGalaxyServer.getInstance();
    private static final long serialVersionUID = 3L;
    
    private String _username;
    private String _passhash, _emailAddress;
    
    private UserData _userdata;
    
    private transient String _securityToken;
    private transient Date _securityTokenCreated;
    private transient TokenReason _tokenReason;
    private transient String _tokenSubreason;
    
    private transient HostedConnection _connection;
    private transient Channel _channel;
    private ChannelToken _lastChannelToken;
    
    static enum TokenReason {
	CHANGE_PASSWORD, CHANGE_ADDRESS, TEMPORARY_PASSWORD;
    }
    
    static enum TokenError { 
	NO_MATCH, TIMEOUT;
    }
    
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * @deprecated serialization only
     */
    @Deprecated Account() {}
    
    Account(String username, String password, String emailAddress) {
	_username = username;
	_emailAddress = emailAddress;
	if(password != null)
	    changePassword(password);
	else
	    _passhash = null;
	_userdata = new UserData(username, false);
    }
    
    private Account(String username, String hash, String emailAddress, Map<String, String> userdata) {
	_username = username;
	_emailAddress = emailAddress;
	_passhash = hash;
	_userdata = new UserData(username, false, userdata);
    }
    
    static String securityToken() {
	String token = Long.toString(Math.abs(random.nextLong()), 36)
		.substring(0, 6).toUpperCase();
	return token;
    }
    
    public String getUsername() {
	return _username;
    }
    
    public void receiveMessage(Message message) {
	_server.broadcast(Filters.in(_connection), message);	
    }
    
    public boolean checkPassword(String password) {
	if(AccountManager.isLocalBehavior()) return true;
	if(_passhash == null)
	    return true;
	try {
	    boolean match = PasswordHash.validatePassword(password, _passhash);
	    if(!match && _tokenReason == TokenReason.TEMPORARY_PASSWORD)
		match = (checkSecurityToken(password) == null);
	    return match;
	}
	catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
	    PatchworkGalaxy.writeException(e);
	    return false;
	}
    }
    
    public boolean checkEmail(String email) {
	return _emailAddress.equalsIgnoreCase(email);
    }
    
    TokenError checkSecurityToken(String token) {
	if(new Date().getTime() > _securityTokenCreated.getTime() + 900000)
	    return TokenError.TIMEOUT;
	if(token.equals(_securityToken))
	    return null;
	else
	    return TokenError.NO_MATCH;
    }
    
    boolean isLoggedIn(HostedConnection connection) {
	return connection.equals(_connection);
    }
    
    void setConnection(HostedConnection connection) {
	if(connection == null && _connection != null) {
	    if(_channel != null) {
		_channel.removeAccount(this, " disconnected");
		_channel = null;
	    }
	    _connection = null;
	    AccountManager.endConnection(_connection);
	}
	else
	    _connection = connection;
    }
    
    public void logout() {
	setConnection(null);
    }
    
    public HostedConnection getConnection() {
	return _connection;
    }
    
    boolean resetPassword(HostedConnection connection) {
	return changePassword(
	    connection,
	    securityToken()
	);
    }
    
    boolean changePassword(HostedConnection connection, String password) { 
	if(!isLoggedIn(connection))
	    return false;
	return changePassword(password);
    }
    
    public String requestPasswordReset(String email) {
	if(isTemporary())
	    return "Can't reset the password for a temporary account";
	if(!email.trim().equalsIgnoreCase(_emailAddress))
	    return "Invalid username/email address combination";
	createSecurityToken(TokenReason.TEMPORARY_PASSWORD);
	_tokenSubreason = hash(_securityToken);
	if(_tokenSubreason == null)
	    return "Error creating password hash (this is bad, contact patrick@hailstorm.net ASAP)";
	else {
	    sendTempPassEmail();
	    return null;
	}
    }
    
    public String requestPasswordChange(String oldPassword, String newPassword) {
	if(checkPassword(oldPassword)) {
	    createSecurityToken(TokenReason.CHANGE_PASSWORD);
	    _tokenSubreason = hash(newPassword);
	    if(_tokenSubreason == null)
		return "Error creating password hash (this is bad, contact patrick@hailstorm.net ASAP)";
	    else {
		sendTokenEmail("A change to your Patchwork Galaxy account password was requested.");
		return null;
	    }
	}
	else
	    return "Old password incorrect, change denied";
    }
    
    public String requestEmailChange(String password, String address) {
	if(checkPassword(password)) {
	    createSecurityToken(TokenReason.CHANGE_ADDRESS);
	    _tokenSubreason = address;
		sendTokenEmail("A change to your Patchwork Galaxy account email address was requested.");
	    return null;
	}
	else
	    return "Password incorrect, change denied";
    }
	
    
    private String hash(String password) {
	try {
	    return PasswordHash.createHash(password);
	} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
	    PatchworkGalaxy.writeException(ex);
	    Logger.getLogger(Account.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
    }
    
    private boolean changePassword(String password) {
	String hash = hash(password);
	if(hash != null) {
	    _passhash = hash;
	    return true;
	}
	else
	    return false;
    }
    
    boolean changeEmail(String password, String address) {
	if(checkPassword(password)) {
	    return true;
	}
	else
	    return false;
    }
    
    public boolean booleanUserDatum(String key) {
	return Boolean.valueOf(_userdata.getDatum(key));
    }
    
    public String getUserDatum(String key, String defaultVal) {
	String result = getUserDatum(key);
	if(result.length() == 0)
	    result = defaultVal;
	return result;
    }
    
    public String getUserDatum(String key) {
	return _userdata.getDatum(key);
    }
    
    public void setUserDatum(String key, String value, Account setter) throws TransactionException {
	if(key == null)
	    return;
	if(value == null)
	    value = "";
	try {
	    UDatManager.getInstance().set(_userdata, key, value, setter == null ? null : setter.getUserData());
	    if(_channel != null)
		_channel.updateUserdata(this, _userdata);
	    writeFile();
	}
	catch(UDatException e) {
	    throw new TransactionException(e.getLocalizedMessage());
	}
    }
    
    public void onJoinedGame() {
	UDatManager.getInstance().onJoinGame(_userdata);
    }
    
    public void onLeaveGame() {
	UDatManager.getInstance().onLeaveGame(_userdata);
    }
    
    public void setChannel(Channel channel) throws TransactionException {
	setChannel(channel, "");
    }
    
    public void setChannel(Channel channel, String reason) throws TransactionException {
	if(_channel == channel)
	    return;
	if(channel != null)
	    channel.addAccount(this);
	if(_channel != null) {
	    _channel.removeAccount(this, reason);
	    ChannelToken token = _channel.getToken();
	    if(token != null)
		_lastChannelToken = token;
	}
	_channel = channel;
    }
    
    boolean isTemporary() {
	return _emailAddress == null;
    }
    
    public void popChannel() throws TransactionException {
	Channel channel = ChannelManager.simple().getChannelFromToken(_lastChannelToken);
	setChannel(channel);
	_lastChannelToken = null;
    }
    
    public Channel getChannel() {
	return _channel;
    }
    
    void createSecurityToken(TokenReason why) {
	_securityToken = securityToken();
	_securityTokenCreated = new Date();
	_tokenReason = why;
    }
    
    void sendTempPassEmail() {
	try {
	    AccountEmailer.sendEmail(_emailAddress, "Patchwork Galaxy account management",
		    "You requested your Patchwork Galaxy account password to be reset."
		    + " You may log in with this temporary password to log in:"
		    + "\n" + _securityToken
		    + "\n\n"
		    + "Once you log in, is it imperative that you change your password."
		    + " This temporary password will expire after 15 minutes."
		    + "\n\n"
		    + "Happy warmongering!"
		    + "\n\t~The Patchwork Galaxy team"
		    + "\n\n\n\n"
		    + "(If you did not request this change, ignore this message, or contact patrick@hailstormstudios.net.)"
	    );
	}
	catch(Exception e) {}
    }
    
    void sendTokenEmail(String message) {
	try {
	    AccountEmailer.sendEmail(_emailAddress, "Patchwork Galaxy account management",
		message
		    + " For security reasons, you will be asked to confirm this change. Enter the following token when prompted:"
		    + "\n" + _securityToken
		    + "\n\n"
		    + "Happy warmongering!"
		    + "\n\t~The Patchwork Galaxy team"
		    + "\n\n\n\n"
		    + "(If you did not request this change, ignore this message, or contact patrick@hailstormstudios.net.)"
	    );
	}
	catch(Exception e) {}
    }
    
    public String acceptSecurityToken(String token) {
	TokenError error = checkSecurityToken(token);
	if(error == null) {
	    if(_tokenReason == TokenReason.CHANGE_ADDRESS)
		_emailAddress = _tokenSubreason;
	    else if(_tokenReason == TokenReason.CHANGE_PASSWORD) {
		if(_tokenSubreason != null)
		    _passhash = _tokenSubreason;
	    }
	    else
		return "Illegal security token type (this is bad, contact patrick@hailstormstudios.net ASAP)";
	    writeFile();
	    return null;
	}
	else {
	    if(error == TokenError.NO_MATCH)
		return "Incorrect security token, change denied";
	    else
		return "Security token timed out, change denied (try resubmitting)";
	}

    }
    
    public UserData getUserData() {
	return new UserData(_userdata);
    }
    
    static Account fromFile(File file) throws IOException {
	try(FileReader reader = new FileReader(file)) {
	    try(BufferedReader br = new BufferedReader(reader)) {
		String username = br.readLine();
		String passhash = br.readLine();
		String email = br.readLine();
		Map<String, String> userdata = new HashMap<>();
		String key, value;
		while((key = br.readLine()) != null) {
		    value = br.readLine();
		    if(value != null) {
			userdata.put(key, value);
		    }
		}
		return new Account(username, passhash, email, userdata);
	    }
	}
    }
    
    void writeFile() {
	if(AccountManager.isLocalBehavior()) return;
	String repr = repr();
	File file = new File("./accounts/" + _username);
	try(FileWriter writer = new FileWriter(file)) {
	    writer.write(repr);
	}
	catch(IOException e) {
	    throw new RuntimeException(e);
	}
    }
    
    private String repr() {
	StringBuilder result = new StringBuilder(_username);
	result.append(System.lineSeparator());
	result.append(_passhash);
	result.append(System.lineSeparator());
	result.append(_emailAddress);
	for(Entry<String, String> i : _userdata.getData().entrySet()) {
	    result.append(System.lineSeparator());
	    result.append(i.getKey());
	    result.append(System.lineSeparator());
	    result.append(i.getValue());
	}
	return result.toString();
    }
    
}