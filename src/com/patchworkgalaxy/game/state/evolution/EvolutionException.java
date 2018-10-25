package com.patchworkgalaxy.game.state.evolution;

public class EvolutionException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public EvolutionException() {}
    
    public EvolutionException(String message) {
	super(message);
    }
    
    public EvolutionException(Throwable cause) {
	super(cause);
    }
    
}
