package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.state.GameState;

class EvolutionAcceptorSimple extends EvolutionAcceptor {
    
    EvolutionAcceptorSimple(GameState gameState) {
	super(gameState);
    }
    
    @Override
    boolean acceptImpl(Evolution evolution, EvolutionStrategy strategy) throws EvolutionException {
	strategy.evolve(this, evolution);
	return true;
    }
    
}
