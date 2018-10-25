package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexParseException;
import java.util.HashSet;
import java.util.Set;

class Variable {
    
    private final String _name;
    private final int _id;
    private final Type _type;
    private final Set<Monitor> _monitors;
    private final Set<Monitor> _fastMonitors;

    Variable(String name, int id, Type type) {
	_name = name;
	_id = id;
	_type = type;
	_monitors = new HashSet<>();
	_fastMonitors = new HashSet<>();
    }
    
    String getName() {
	return _name;
    }
    
    int getId() {
	return _id;
    }
    
    Type getType() {
	return _type;
    }
    
    void typecheckDefault() throws PlexParseException {}
    
    static Variable getInitVariable() {
	return new Variable(Definitions.INIT_KEYWORD, Definitions.INIT_ID, TypeSimple.VOID);
    }
    
    static Variable getDisplayVariable() {
	return new Variable(Definitions.DISPLAY_KEYWORD, Definitions.DISPLAY_ID, TypeSimple.VOID);
    }
    
    static Variable getAnyUpdateVariable() {
	return new Variable(Definitions.ANY_UPDATE_KEYWORD, Definitions.ANY_UPDATE_ID, TypeSimple.VOID);
    }
    
}