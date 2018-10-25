package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.client.ClientManager;

class EvolutionAcceptorClient extends EvolutionAcceptor {
    
    EvolutionAcceptorClient(GameState gameState) {
	super(gameState);
    }
    
    @Override
    boolean acceptImpl(Evolution evolution, EvolutionStrategy strategy) throws EvolutionException {
	if(evolution.isValidated()) {
	    Utils.getEvolutionStrategy(evolution).evolve(this, evolution);
	    return true;
	}
	else {
	    ClientManager.client().send(evolution);
	    return false;
	}
    }
    
}
