package com.patchworkgalaxy.udat;

public class SpecialKeys {
    private SpecialKeys() {}
    
    public static final String
	    TYPE_INT = "int",
	    TYPE_FLOAT = "float",
	    TYPE_BOOLEAN = "boolean",
	    TYPE_STRING = "string"
	    ;
    
    public static final String
	    TYPE_ACCOUNT = "Account",
	    
	    USERNAME = "account.username",
	    CHANNEL = "account.channel",
	    
	    FACTION = "game.faction",
	    READY = "game.ready",
	    HOST = "game.host",
	    OBSERVER = "game.observer",
	    
	    WINS = "record.wins",
	    LOSSES = "record.losses",
	    DRAWS = "record.draws",
	    
	    COLOR = "mod.color",
	    SQUELCH_UNTIL = "mod.squelch",
	    SUSPEND_UNTIL = "mod.suspend",
	    
	    MOD_RIGHTS = "admin.rights.mod",
	    ADMIN_RIGHTS = "admin.rights.admin",
	    BANNED = "admin.rights.banned",
	    
	    IFF_FRIENDLY = "relations.friend",
	    IFF_HOSTILE = "relations.foe"
	    ;
    
    public static final String
	    TYPE_CHANNEL = "Channel",
	    
	    CHANNELNAME = "channel.name",
	    OPERATOR = "channel.operator",
	    PUBLIC = "channel.public",
	    PASSWORD = "channel.password",
	    
	    IS_GAME = "game.isgame",
	    ALLOWS_SPECTATORS = "game.spectators"
	    ;
    
}
