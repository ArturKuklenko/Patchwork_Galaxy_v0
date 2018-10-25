package com.patchworkgalaxy.network;

import static com.patchworkgalaxy.network.Definitions.*;
import com.patchworkgalaxy.plex.BuildContext;
import com.patchworkgalaxy.plex.Context;
import com.patchworkgalaxy.plex.VariableDeclaration;
import com.patchworkgalaxy.plex.exceptions.PlexDeserializationException;
import java.util.List;

class ChannelContextFactory {
    private ChannelContextFactory() {}
    
    private static final Context _prototypeContext;
    static {
	try {
	    BuildContext build = new BuildContext();
	    build.defineCategory(TYPE_ACCOUNT,
		    new VariableDeclaration(USERNAME, "string"),
		    new VariableDeclaration(FACTION, "string"),
		    new VariableDeclaration(READY, "boolean"),
		    new VariableDeclaration(HOST, "boolean"),
		    new VariableDeclaration(OBSERVER, "boolean"),
		    new VariableDeclaration(WINS, "int"),
		    new VariableDeclaration(LOSSES, "int"),
		    new VariableDeclaration(DRAWS, "int"),
		    new VariableDeclaration(COLOR, "int"),
		    new VariableDeclaration(SQUELCH_UNTIL, "int"),
		    new VariableDeclaration(SUSPEND_UNTIL, "int"),
		    new VariableDeclaration(MOD_RIGHTS, "boolean"),
		    new VariableDeclaration(ADMIN_RIGHTS, "boolean"),
		    new VariableDeclaration(BANNED, "boolean")
	    );
	    build.defineCategory(TYPE_CHANNEL,
		    new VariableDeclaration(CHANNELNAME, "string"),
		    new VariableDeclaration(OPERATOR, "boolean"),
		    new VariableDeclaration(PUBLIC, "boolean"),
		    new VariableDeclaration(PASSWORD, "string"),
		    new VariableDeclaration(IS_GAME, "boolean"),
		    new VariableDeclaration(MAX_PLAYERS, "int"),
		    new VariableDeclaration(ALLOWS_SPECTATORS, "boolean")
	    );
	    _prototypeContext = build.getContext();
	} catch(Exception e) { throw new RuntimeException(e); }
    }
    
    private static Context getChannelContext(List<String> records) {
	try {
	    return _prototypeContext.load(records);
	} catch(PlexDeserializationException e) { throw new RuntimeException(e); }
    }
    
}
