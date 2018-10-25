package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.ArrayList;
import java.util.List;

class Record implements Datum {
    
    private final Category _category;
    private final String _signature;
    private final boolean _isNamed;
    
    private final List<Datum> _data;
    
    private static int _autosign;
    
    Record(Category category) {
	this(category, null);
    }
    
    Record(Category category, String signature) {
	_category = category;
	_data = new ArrayList<>();
	padData();
	if(signature == null) {
	    _isNamed = false;
	    _signature = (++_autosign + "");
	}
	else {
	    _isNamed = true;
	    _signature = signature;
	}
    }
    
    Record(Record copyOf) {
	_category = copyOf._category;
	_signature = copyOf._signature;
	_isNamed = copyOf._isNamed;
	_data = new ArrayList<>(copyOf._data);
	padData();
    }
    
    private void padData() {
	int delta = _category.getVariableCount() - _data.size();
	while(--delta >= 0)
	    _data.add(null);
    }

    @Override
    public Category getType() {
	return _category;
    }
    
    int getDatumId(String variable) throws PlexTypeException {
	return _category.getVarId(variable);
    }

    @Override
    public String getValue() {
	return "" + _signature;
    }
    
    Datum access(int key) throws PlexTypeException {
	if(key < 0 || key >= _data.size())
	    throw new PlexTypeException("Key " + key + " out of bounds for " + _category);
	Datum datum = _data.get(key);
	if(datum != null)
	    return datum;
	return _category.getDefault(key);
    }
    
    Datum access(String name) throws PlexTypeException {
	int id = _category.getVarId(name);
	Datum datum = access(id);
	return datum;
    }
    
    String accessValue(String name) throws PlexTypeException {
	Datum datum = access(name);
	if(datum != null)
	    return datum.getValue();
	else
	    return "";
    }
    
    void setValue(AbstractContext context, int key, String value) throws PlexTypeException {
	Type type = _category.getDeclaredType(key);
	if(type == null)
	    throw new PlexTypeException("Key " + key + " out of bounds for " + _category);
	Datum datum = type.createDatum(value, context);
	_data.set(key, datum);
    }
    
    void ensureType(Type type, int key) throws PlexTypeException {
	Type type2 = _category.getDeclaredType(key);
	type.isAssignableAs(type2);
    }
    
    void merge(Record other) {
	if(!_signature.equals(other._signature))
	    throw new IllegalArgumentException();
	if(_category != other._category)
	    throw new IllegalArgumentException();
	for(int i = 0; i < _data.size(); ++i)
	    _data.set(i, other._data.get(i));
    }
    
    String getSignature() {
	return _signature;
    }
    
    @Override
    public String toString() {
	StringBuilder result = new StringBuilder();
	result.append("Record[").append(_category).append("]");
	for(Datum datum : _data)
	    result.append("\n\t").append(datum == null ? "null" : datum.getValue());
	return result.toString();
    }
    
    List<String> dump(List<String> to) {
	List<String> result = to == null ? new ArrayList<String>() : to;
	result.add(_category.getName());
	result.add(_signature);
	for(Datum datum : _data) {
	    String s = datum == null ? null : datum.getValue();
	    if(s == null)
		result.add(Definitions.NULL_CHAR);
	    else
		result.add(s);
	}
	return result;
    }
    
}
