package com.patchworkgalaxy.general.data;

import com.patchworkgalaxy.game.state.GameState;

public class Resolver implements Numeric {
    
    private final GameState _gameState;
    private final String _path;
    
    private final boolean _caches;
    private Object _cache;
    
    public Resolver(GameState gameState, String path, boolean caches) {
	_gameState = gameState;
	_path = path;
	_caches = caches;
    }
    
    public Resolver(Object force) {
	_gameState = null;
	_path = null;
	_caches = false;
	_cache = force;
    }
    
    public static Resolver createResolver(GameState gameState, String path, boolean caches) {
	if(path == null)
	    return null;
	else {
	    try {
		return new Resolver(Float.valueOf(path));
	    }
	    catch(NumberFormatException e) {
		return new Resolver(gameState, path, caches);
	    }
	}
    }
    
    /**
     * Looks up this resolver's path in its game state. If the resolver caches,
     * the result is stored and subsequent lookups will use the stored value.
     * <p>
     * There are times when it is impossible to set a resolver's game state
     * in the constructor (eg. parsing templates). A var-arg parameter is used
     * to handle these cases. If a single game state is provided and this
     * resolver doesn't have its own game state, that game state is used.
     * If the resolver does have its own game state and they differ, an
     * IllegalArgumentException is thrown. <em>This is temporary.</em>
     * </p>
     * @param gameState fallback game state
     * @return the looked-up value
     * @throws ClassCastException if an object of the wrong type is found
     */
    public Object resolve(GameState... gameState) {
	GameState gs = _gameState;
	if(gameState.length > 0) {
	    if(_gameState != null && _gameState != gameState[0])
		throw new IllegalArgumentException("Fallback game state set, but already have a different game state");
	    gs = gameState[0];
	}
	if(_cache != null)
	    return _cache;
	else {
	    Object found = gs.lookup(_path);
	    if(_caches)
		_cache = found;
	    return found;
	}
    }
    
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type, GameState... gameState) {
	Object o = resolve(gameState);
	if(type.isInstance(o))
	    return (T)o;
	else
	    return null;
    }

    @Override
    public float toFloat(GameState gameState) {
	Object o = resolve(gameState);
	if(o instanceof Numeric)
	    return ((Numeric)o).toFloat(gameState);
	else if(o instanceof Number)
	    return ((Number)o).floatValue();
	else
	    return o == null ? 0 : 1;
    }
    
}
