package com.patchworkgalaxy.network.server;

import com.patchworkgalaxy.network.server.account.Account;

public class ConsoleCommands {
    
    private ConsoleCommands() {}
    
    static String checkMessage(Account account, String message) {
	if(message.length() == 0 || message.charAt(0) != '/')
	    return null;
	String[] tokens = message.split("\\s+");
	String command = tokens[0].substring(1).toLowerCase();
	if(command.equals("concede")) {
	    return "This command has been temporarily disabled.";
	}
	return "Unknown command " + command;
    }
    
}
