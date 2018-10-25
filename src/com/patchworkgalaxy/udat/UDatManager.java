package com.patchworkgalaxy.udat;

import com.patchworkgalaxy.template.TemplateRegistry;

public class UDatManager {
    
    private static UDatManager _instance;
    private UDatManager() {}
    
    public static UDatManager getInstance() {
	if(_instance == null)
	    _instance = new UDatManager();
	return _instance;
    }
    
    public void set(UserData mutate, String key, String value, UserData requestedBy) throws UDatException {
	//TODO: dummy implementation
	UDMDummy.dummyset(mutate, key, value, requestedBy);
    }
    
    public void onJoinGame(UserData mutate) {
	try {
	    set(mutate, SpecialKeys.FACTION, TemplateRegistry.FACTIONS.lookup(0).getName(), null);
	    set(mutate, SpecialKeys.READY, "false", null);
	}
	catch(UDatException e) {
	    throw new RuntimeException(e);
	}
    }
    
    public void onLeaveGame(UserData mutate) {
	try {
	    set(mutate, SpecialKeys.OBSERVER, "false", null);
	    set(mutate, SpecialKeys.HOST, "false", null);
	}
	catch(UDatException e) {}
    }
    
}
