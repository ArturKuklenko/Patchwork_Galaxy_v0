package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.HashSet;
import java.util.Set;

public abstract class EvolutionAcceptor {
    
    private final GameState _gameState;
    private boolean _update;
    private final Set<Coordinate> _selected;
    
    EvolutionAcceptor(GameState gameState) {
	_gameState = gameState;
	_selected = new HashSet<>();
    }

    abstract boolean acceptImpl(Evolution evolution, EvolutionStrategy strategy) throws EvolutionException;
    
    public static enum Type {
	SIMPLE, CLIENT, SERVER, AI;
    }
	
    public static EvolutionAcceptor create(GameState gameState, Type type) {
        switch(type) {
	case SIMPLE:
	    return new EvolutionAcceptorSimple(gameState);
	case CLIENT:
	    return new EvolutionAcceptorClient(gameState);
	case SERVER:
	    return new EvolutionAcceptorServer(gameState);
	case AI:
	    return new EvolutionAcceptorAI(gameState);
	default:
	    throw new IllegalArgumentException();
	}
    }
    
    public final void accept(Evolution evolution, GameState gameState) throws EvolutionException {
	EvolutionStrategy evolutionStrategy = Utils.getEvolutionStrategy(evolution);
	if(evolutionStrategy instanceof EvolutionStrategy.UpdatesMap)
	    _update = true;
	else if(_update)
	    _gameState.getBoard().updateAdjacencies();
	if(acceptImpl(evolution, evolutionStrategy)) {
	    long seedUpdate = evolution.getSeedUpdate();
	    if(seedUpdate != 0)
		gameState.setRngSeed(evolution.getSeedUpdate());
	}
    }
    
    final Set<Coordinate> getSelectedCoordinates() {
	return new HashSet<>(_selected);
    }
    
    final void setSelectedCoordinates(Set<Coordinate> coordinates) {
	_selected.clear();
	if(coordinates != null)
	    _selected.addAll(coordinates);
    }
    
    final GameState getGameState() {
	return _gameState;
    }
    
    final Set<Tile> getSelectedTiles() {
	Set<Tile> result = new HashSet<>();
	for(Coordinate coordinate : new HashSet<>(_selected)) {
	    Tile tile = _gameState.tileAtCoordinates(coordinate.x, coordinate.y);
	    if(tile != null)
		result.add(tile);
	}
	return result;
    }
    
}
