package com.patchworkgalaxy.game.state;

import com.patchworkgalaxy.game.state.evolution.EvolutionAcceptor;

public class ClientGameState extends GameState {
    
    private static ClientGameState _currentInstance;
    
    private ClientGameState(GameHistory history) {
	super(history, EvolutionAcceptor.Type.CLIENT, true);
    }
    
    public static void setupNewInstance(GameHistory history) {
	_currentInstance = new ClientGameState(history);
    }
    
    public static ClientGameState getCurrentInstance() {
	return _currentInstance;
    }
    
}
