package com.patchworkgalaxy.game.tile;

import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileCollector;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.general.data.Numeric;

public class Rangefinder implements Namespace, Numeric {

    private final Tile _start;
    private Namespace _currentNamespace;
    
    public Rangefinder(GameComponent start) {
	_start = start.getPosition();
	_currentNamespace = start;
    }
    
    @Override
    public Object lookup(String name) {
	if(_currentNamespace != null) {
	    Object o = _currentNamespace.lookup(name);
	    _currentNamespace = (o instanceof Namespace ? (Namespace)o : null);
	}
	return this;	
    }

    @Override
    public float toFloat(GameState gameState) {
	
	if(!(_currentNamespace instanceof GameComponent))
	    return Float.NaN;
	
	Tile destination = ((GameComponent)_currentNamespace).getPosition();
	if(destination == null)
	    return Float.NaN;	
	return TileCollector.rangefinder(_start, destination);
	
    }
    
}
