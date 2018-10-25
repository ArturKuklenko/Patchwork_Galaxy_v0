package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.state.GameState;

class EvolutionAcceptorServer extends EvolutionAcceptor {
    
    EvolutionAcceptorServer(GameState gameState) {
	super(gameState);
    }
    
    @Override
    boolean acceptImpl(Evolution evolution, EvolutionStrategy strategy) throws EvolutionException {
	try {
	    strategy.evolve(this, evolution);
	    return true;
	}
	catch(EvolutionException e) {
	    return false;
	}
    }
    
}
