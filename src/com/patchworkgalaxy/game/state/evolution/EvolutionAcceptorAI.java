package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.state.GameState;

class EvolutionAcceptorAI extends EvolutionAcceptor {
    
    EvolutionAcceptorAI(GameState gameState) {
	super(gameState);
    }
    
    @Override
    boolean acceptImpl(Evolution evolution, EvolutionStrategy strategy) throws EvolutionException {
	throw new UnsupportedOperationException();
    }
    
}
