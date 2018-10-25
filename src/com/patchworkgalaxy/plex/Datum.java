package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexParseException;

interface Datum {
    
    Type getType();
    
    String getValue();
    
    static class Literal implements Datum {

	private final Type _type;
	private final String _value;
	
	Literal(Type type, String value) {
	    _type = type;
	    _value = value;
	}
	
	static Datum parse(String value) throws PlexParseException {
	    return new Literal(TypeSimple.STRING, value);
	    //TODO: some things aren't strings
	}
	
	@Override
	public Type getType() {
	    return _type;
	}

	@Override
	public String getValue() {
	    return _value;
	}
	
    }
    
}
