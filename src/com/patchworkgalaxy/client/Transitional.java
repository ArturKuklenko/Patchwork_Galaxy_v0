package com.patchworkgalaxy.client;

import com.patchworkgalaxy.Effect;
import com.patchworkgalaxy.Effort;
import com.patchworkgalaxy.network.oldmessages.ProtocolCallbacks;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.network.transaction.TransactionType;

class Transitional {
    
    private final PWGClient _client;
    
    Transitional(PWGClient client) {
	_client = client;
    }
    
    void translate(ProtocolMessage oldMessage, ProtocolCallbacks oldCallbacks) {
	Object[] foo = translateMessage(oldMessage);
	TransactionType type = (TransactionType)foo[0];
	String[] args = (String[])foo[1];
	Effort effort = _client.transaction(type, args);
	attachTranslatedCallbacks(effort, oldCallbacks);
    }
    
    private static Object[] translateMessage(ProtocolMessage oldMessage) {
	String foo = oldMessage.getArgs().trim();
	String[] args;
	if(foo.length() > 0)
	    args = foo.split(";;");
	else
	    args = new String[0];
	TransactionType type;
	switch(oldMessage.getOpcode()) {
	case "LGIN":
	    type = TransactionType.LOGIN;
	    break;
	case "AREG":
	    type = TransactionType.BEGIN_REGISTRATION;
	    break;
	case "AFIN":
	    type = TransactionType.FINISH_REGISTRATION;
	    break;
	case "ARES":
	    type = TransactionType.RESET_PASSWORD;
	    break;
	case "JOIN":
	    if(args[0].startsWith("game:")) {
		args[0] = args[0].substring(5);
		type = TransactionType.JOIN_GAME;
	    }
	    else
	        type = TransactionType.JOIN_CHANNEL;
	    break;
	case "JNEW":
	    if(args[0].startsWith("game:")) {
		args[0] = args[0].substring(5);
		type = TransactionType.CREATE_GAME;
	    }
	    else
	        type = TransactionType.CREATE_CHANNEL;
	    break;
	case "JBAK":
	    type = TransactionType.JOIN_PREVIOUS_CHANNEL;
	    break;
	case "UDAT":
	    type = TransactionType.SET_USER_DATA;
	    break;
	case "GAME":
	    type = TransactionType.START_GAME;
	    break;
	case "LIST":
	    type = args[0].equalsIgnoreCase("games") ? TransactionType.LIST_PUBLIC_GAMES : TransactionType.LIST_PUBLIC_CHANNELS;
	    args = new String[0];
	    break;
	case "UMAN":
	    switch(args[0]) {
	    case "Password":
		type = TransactionType.REQUEST_PASSWORD_CHANGE;
		break;
	    case "Email":
		type = TransactionType.REQUEST_EMAIL_CHANGE;
		break;
	    case "Finish":
		type = TransactionType.USE_SECURITY_TOKEN;
		break;
	    default:
		throw new IllegalArgumentException(args[0]);
	    }
	    String[] args2 = new String[args.length - 1];
	    System.arraycopy(args, 1, args2, 0, args2.length);
	    args = args2;
	    break;
	default:
	    throw new IllegalArgumentException("unsupported opcode " + oldMessage.getOpcode());
	}
	return new Object[] {type, args};
    }
    
    private void attachTranslatedCallbacks(Effort effort, final ProtocolCallbacks oldCallbacks) {
	if(oldCallbacks == null) return;
	effort.then(new Effect<Object>() {
	    @Override public void execute(Object input) {
		oldCallbacks.succeed(new ProtocolMessage("VLID", input.toString()));
	    }
	}).onError(new Effect<Throwable>() {
	    @Override public void execute(Throwable input) {
		oldCallbacks.fail(new ProtocolMessage("FAIL", input.getLocalizedMessage()));
	    }
	});
    }
    
}
