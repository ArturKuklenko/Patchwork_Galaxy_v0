package com.patchworkgalaxy.game.tile;

import java.util.HashSet;
import java.util.Set;

public class TileCollector {
    
    private final Set<Tile> _collection;
    
    private int _steps;
    private final int _maxSteps;
    
    private final TileFilter _stepRule, _destination;
    
    public TileCollector(Tile source, int maxSteps, TileFilter stepRule, TileFilter destination) {
	_maxSteps = maxSteps;
	_stepRule = stepRule;
	_destination = destination;
	_collection = new HashSet<>();
	collect(source);
    }
    
    public TileCollector(Tile source, int maxSteps) {
	this(source, maxSteps, null, null);
    }
    
    public TileCollector(Tile source, int maxSteps, TileFilter destination) {
	this(source, maxSteps, null, destination);
    }
    
    public TileCollector(Tile source, TileFilter destination) {
	this(source, null, destination);
    }
    
    public TileCollector(Tile source, TileFilter stepRule, TileFilter destination) {
	this(source, Integer.MAX_VALUE, stepRule, destination);
    }
    
    public static int rangefinder(Tile source, Tile destination) {
	return new TileCollector(source, TileFilter.isTile(destination)).getDistance();
    }
    
    public Set<Tile> getTiles() {
	return new HashSet<>(_collection);
    }
    
    public int getDistance() {
	return _steps;
    }
    
    private void collect(Tile source) {
	_collection.add(source);
	if(!(_destination != null && _destination.filter(source))) {
	    Set<Tile> a = new HashSet<>();
	    Set<Tile> b = new HashSet<>(_collection);
	    step(a, b);
	}
    }
    
    private void step(Set<Tile> store, Set<Tile> prev) {
	if(!doStep(store, prev)) {
	    step(prev, store);
	}
    }
    
    private boolean doStep(Set<Tile> store, Set<Tile> prev) {
	if(++_steps > _maxSteps)
	    return true;
	store.clear();
	for(Tile tile : prev) {
	    for(Tile tile2 : tile.getAdjacency())
		if(add(tile2))
		    store.add(tile2);
	}
	if(_destination != null) {
	    for(Tile tile : store)
		if(_destination.filter(tile))
		    return true;
	}
	return store.isEmpty();
    }
    
    private boolean add(Tile tile) {
	if(tile == null)
	    return false;
	if(_stepRule != null && !_stepRule.filter(tile))
	    return false;
	return _collection.add(tile);
    }
    
}
