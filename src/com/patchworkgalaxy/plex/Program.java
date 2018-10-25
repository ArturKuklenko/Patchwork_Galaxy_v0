package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Program {

    private final List<Category> _categories;
    private final Map<String, Integer> _catIdsByName;
    
    private final MonitorGroup _rules;
    
    Program(Collection<Category> categories) {
	_categories = new ArrayList<>();
	_catIdsByName = new HashMap<>();
	int i = 0;
	for(Category category : categories) {
	    _categories.add(category);
	    _catIdsByName.put(category.getName(), i);
	    ++i;
	}
	_rules = new MonitorGroup();
    }
    
    public Context getContext() {
	return new ContextStandard(this);
    }
    
    Category getCategory(int id) {
	return _categories.get(id);
    }
    
    int getCategoryId(String name) throws PlexTypeException {
	if(!_catIdsByName.containsKey(name))
	    throw new PlexTypeException("Unknown type " + name);
	return _catIdsByName.get(name);
    }
    
    Category getCategory(String name) throws PlexTypeException {
	int id = getCategoryId(name);
	return _categories.get(id);
    }
    
    void pokeRules(ContextScratch context, Set<DatumSpecifier> changed) throws PlexException {
	_rules.pokeMonitors(context, changed);
    }
    
}
