package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexParseException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Category implements Type {
    
    private final String _name;
    private final List<Variable> _variables;
    private final Map<String, Integer> _varIdsByName;
    
    private final Variable _initVar, _displayVar, _anyVar;
    
    Category(String name) {
	_name = name;
	_variables = new ArrayList<>();
	_varIdsByName = new HashMap<>();
	_varIdsByName.put(Definitions.ANY_UPDATE_KEYWORD, Definitions.ANY_UPDATE_ID);
	_initVar = Variable.getInitVariable();
	_displayVar = Variable.getDisplayVariable();
	_anyVar = Variable.getAnyUpdateVariable();
    }
    
    @Override
    public String getNullValue() {
	return "-1";
    }
    
    public String getName() {
	return _name;
    }
    
    @Override
    public String toString() {
	return "PlexCategory[" + _name + "]";
    }

    @Override
    public void isAssignableAs(Type other) throws PlexTypeException {
	if(other != this)
	    throw new PlexTypeException("Category type " + _name + " is only compatible with itself, not " + other);
    }
    
    Variable getDeclaredVariable(int id) {
	switch(id) {
	case Definitions.INIT_ID:
	    return _initVar;
	case Definitions.DISPLAY_ID:
	    return _displayVar;
	case Definitions.ANY_UPDATE_ID:
	    return _anyVar;
	default:
	    return _variables.get(id);
	}
    }    
    
    Type getDeclaredType(int id) {
	return getDeclaredVariable(id).getType();
    }
    
    Datum getDefault(int id) {
	return null;
    }
    
    void hasVariable(String name, Type type) throws PlexParseException {
	if(_varIdsByName.containsKey(name))
	    throw new PlexParseException(this + ": tried to redeclare variable " + name);
	int id = _variables.size();
	if(name.equals(Definitions.INIT_KEYWORD)) {
	    _varIdsByName.put(name, _initVar.getId());
	    return;
	}
	if(name.equals(Definitions.DISPLAY_KEYWORD)) {
	    _varIdsByName.put(name, _displayVar.getId());
	    return;
	}
	Variable variable = new Variable(name, id, type);
	_variables.add(variable);
	_varIdsByName.put(name, id);
    }

    @Override
    public Datum createDatum(String value, AbstractContext context) throws PlexTypeException {
	try {
	    Record record = context.getRecord(value);
	    Type type = record.getType();
	    if(type != this)
		throw new PlexTypeException(value + " is a valid signature but wrong record type (expected " + this + ", got " + type + ")");
	    return record;
	} catch(NumberFormatException e) {
	    throw new PlexTypeException(value + " is not a valid signature (not an integer)");
	} catch(PlexRecordException e) {
	    throw new PlexTypeException(value + " is not a valid signature (couldn't find a record)" );
	}
    }
    
    int getVariableCount() {
	return _variables.size();
    }
    
    int getVarId(String name) throws PlexTypeException {
	if(!_varIdsByName.containsKey(name))
	    throw new PlexTypeException("Unknown variable " + _name + "->" + name);
	return _varIdsByName.get(name);
    }
    
    boolean isHasInitVar() {
	return _initVar != null;
    }
    
    boolean isHasDisplayVar() {
	return _displayVar != null;
    }
    
    int countVariables() {
	return _variables.size();
    }
    
}
