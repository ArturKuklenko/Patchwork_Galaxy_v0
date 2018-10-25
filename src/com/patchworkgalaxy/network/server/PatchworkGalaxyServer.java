package com.patchworkgalaxy.network.server;

import com.jme3.app.SimpleApplication;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filter;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.kernel.KernelException;
import com.jme3.system.JmeContext;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.general.util.XORShiftRandom;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.network.server.account.Account;
import com.patchworkgalaxy.network.server.account.AccountManager;
import com.patchworkgalaxy.network.server.channel.Channel;
import com.patchworkgalaxy.network.transaction.NetTransaction;
import com.patchworkgalaxy.network.transaction.Response;
import com.patchworkgalaxy.network.transaction.TransactionException;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatchworkGalaxyServer extends SimpleApplication implements MessageListener<HostedConnection>, ConnectionListener {


    private static PatchworkGalaxyServer _instance;
    private final Random _rng;
    
    Server conn;
    
    private PatchworkGalaxyServer() {
	_rng = new XORShiftRandom();
    }
    
    public static PatchworkGalaxyServer getInstance() {
	if(_instance == null)
	    _instance = new PatchworkGalaxyServer();
	return _instance;
    }
    
    @Override
    public void simpleInitApp() {
	try {
	    conn = Network.createServer(41342);
            conn.start();
	    conn.addMessageListener(this, ProtocolMessage.class);
	    conn.addMessageListener(this, Evolution.class);
	    conn.addMessageListener(this, ChatMessage.class);
	    conn.addMessageListener(this, NetTransaction.class);
	    conn.addConnectionListener(this);
	    TemplateRegistry.init();
	    if(!AccountManager.isLocalBehavior())
		new File("./accounts").mkdir();
        }
	catch (IOException ex) {
            Logger.getLogger(PatchworkGalaxyServer.class.getName()).log(Level.SEVERE, null, ex);
        }
	catch(KernelException e) {}
    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {
	if(m instanceof NetTransaction) {
	    NetTransaction transaction = (NetTransaction)m;
	    try {
		Object result = new ServerTransactionHandler(source, transaction).handle();
		if(result == null)
		    result = "";
		Response response = transaction.getSuccessfulResponse(result);
		broadcast(Filters.in(source), response);
	    }
	    catch(TransactionException e) {
		Response response = transaction.getErrorResponse(e);
		broadcast(Filters.in(source), response);
	    }
	    return;
	}
	Account sourceAccount = AccountManager.getAccount(source);
	if(sourceAccount == null) {
	    return;
	}
        Channel channel = sourceAccount.getChannel();
	if(channel == null)
	    return;
        if(m instanceof Evolution) {
	    Evolution evolution = (Evolution)m;
	    boolean discrete = evolution.isDiscrete();
	    short seed = (short)_rng.nextInt();
	    evolution = evolution.withRngSeed(seed).withValidationState(true);
            transmit(sourceAccount, evolution, false, !discrete);
	}
	if(m instanceof ChatMessage) {
	    ChatMessage cm = (ChatMessage)m;
	    if(cm.getMessage().length() == 0)
		return;
	    String commandResult = ConsoleCommands.checkMessage(sourceAccount, cm.getMessage());
	    if(commandResult != null) {
		if(commandResult.length() > 0) {
		    ChatMessage reply = new ChatMessage("", commandResult, "ffff00");
		    reply.suppressColon();
		    transmit(sourceAccount, reply, false, true);
		}
	    }
	    else {
		cm.setSender(sourceAccount.getUsername());
		if(cm.hasColon())
		    cm.setMessage(": " + cm.getMessage());
		channel.receive(cm);
	    }
	}
    }
    
    public void broadcast(Filter<HostedConnection> filter, Message m) {
	conn.broadcast(filter, m);
    }
    
    void transmit(Account target, Message m, boolean excludeTarget, boolean includeChannel) {
	if(!excludeTarget && includeChannel) {
	    target.getChannel().transmitToAll(m);
	    return;
	}
	if(!excludeTarget) {
	    target.receiveMessage(m);
	}
	if(includeChannel) {
	    Channel channel = target.getChannel();
	    if(channel != null)
		channel.transmitExcluding(m, target);
	}
    }
    
    @Override
    public void connectionAdded(Server server, HostedConnection connection) { }

    @Override
    public void connectionRemoved(Server server, HostedConnection connection) {
	Account account = AccountManager.getAccount(connection);
	if(account != null)
	    account.logout();
    }

    public static void enableLocalServer() {
	(new Thread(new Runnable() {
	    @Override public void run() {
		(new PatchworkGalaxyServer()).start(JmeContext.Type.Headless);
	    }
	})).start();
    }
    
    public static void main(String[] args) {
        PatchworkGalaxy.main(new String[] { "server" });
	for(String arg : args) {
	    if(arg.equals("local"))
		AccountManager.enableLocalBehavior();
	}
    }
    
}
