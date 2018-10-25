package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.HashMap;
import java.util.Map;

enum TypeSimple implements Type {

    STRING,
    FLOAT,
    INT,
    BOOLEAN,
    VOID,
    ;
    
    private static final Map<String, Type> _simpleTypesByName;
    static {
	_simpleTypesByName = new HashMap<>();
	for(TypeSimple type : values())
	    _simpleTypesByName.put(type.toString().toLowerCase(), type);
    }
    
    static Type get(String name) {
	return _simpleTypesByName.get(name);
    }

    @Override
    public void isAssignableAs(Type other) throws PlexTypeException {
	if(this == INT && other == FLOAT) return;
	if(other == STRING || other == VOID) return;
	if(other != this)
	    throw new PlexTypeException("Incompatible types: " + this + " vs " + other);
    }

    @Override
    public Datum createDatum(String value, AbstractContext context) throws PlexTypeException {
	validateLiteral(value);
	return new Datum.Literal(this, value);
    }

    void validateLiteral(String literal) throws PlexTypeException {
	switch(this) {
	case STRING:
	    break;
	case FLOAT:
	    try {
		Float.valueOf(literal);
	    } catch(NumberFormatException e) { throw new PlexTypeException("Bad float " + literal); }
	    break;
	case INT:
	    try {
		Integer.valueOf(literal);
	    } catch(NumberFormatException e) { throw new PlexTypeException("Bad integer " + literal); }
	    break;
	case BOOLEAN:
	    if(!literal.isEmpty() && !literal.equalsIgnoreCase("true") && !literal.equalsIgnoreCase("false"))
		throw new PlexTypeException("Bad boolean " + literal);
	    break;
	case VOID:
	    throw new PlexTypeException("Void literal not allowed");
	default:
	    throw new AssertionError("Literal parsing not specified for " + this);
	}
    }

    @Override
    public String getNullValue() {
	switch(this) {
	case STRING:
	case VOID:
	    return "";
	case INT:
	case FLOAT:
	    return "0";
	case BOOLEAN:
	    return "false";
	default:
	    throw new AssertionError("Null value not specified for " + this);
	}
    }

    @Override
    public String getName() {
	return toString().toLowerCase();
    }
    
}
