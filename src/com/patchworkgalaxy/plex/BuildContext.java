package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexParseException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BuildContext {
    
    private final Map<String, Category> _namedCategories;
    private final List<Category> _categories;
    private final Set<String> _stubs;
    
    public BuildContext() {
	_namedCategories = new HashMap<>();
	_categories = new ArrayList<>();
	_stubs = new HashSet<>();
    }
    
    Type getType(String name) throws PlexTypeException {
	Type result = TypeSimple.get(name);
	if(result == null) {
	    result = _namedCategories.get(name);
	    if(result == null) {
		result = new Category(name);
		_namedCategories.put(name, (Category)result);
		_categories.add((Category)result);
		_stubs.add(name);
	    }
	}
	return result;
    }
    
    public void defineCategory(String name, List<VariableDeclaration> variables) throws PlexException {
	if(_namedCategories.containsKey(name) && !_stubs.contains(name))
	    throw new PlexParseException("Attempted to redefine category " + name);
	Type category = getType(name);
	if(!(category instanceof Category))
	    throw new PlexParseException("Attempted to redefine simple type" + name);
	for(VariableDeclaration declaration : variables) {
	    Type type = getType(declaration.getTypeName());
	    ((Category)category).hasVariable(declaration.getName(), type);
	}
	_stubs.remove(name);
    }
    
    public void defineCategory(String name, VariableDeclaration... variables) throws PlexException {
	defineCategory(name, Arrays.asList(variables));
    }
    
    public Context getContext() throws PlexParseException {
	if(!_stubs.isEmpty()) {
	    StringBuilder log = new StringBuilder("Attempted to get a context, but category stubs remain:");
	    for(String stub : _stubs)
		log.append(stub).append(" ");
	    throw new PlexParseException(log.toString());
	}
	return new ContextStandard(new Program(_categories));
    }
    
}
