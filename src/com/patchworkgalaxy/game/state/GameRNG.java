package com.patchworkgalaxy.game.state;

import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.general.util.XORShiftRandom;
import java.util.Random;

class GameRNG {
    
    private final XORShiftRandom _game, _notGame;
    
    GameRNG() {
	this(0xDEADBEEF);
    }
    
    GameRNG(long seed) {
	_game = new XORShiftRandom(seed);
	_notGame = new XORShiftRandom(seed);
    }
    
    void setSeed(long seed) {
	_game.setSeed(seed);
	_notGame.setSeed(seed);
    }
    
    double roll(Object requestor) {
	if(requestor instanceof GameEvent) {
	    GameEvent event = (GameEvent)requestor;
	    if(event.getSuccessChance() >= 1 || event.isVirtual())
		return 1;
	    else
		return _game.nextDouble();
	}
	else
	    return _notGame.nextDouble();
    }
    
    int getInt(int max) {
	return _game.nextInt(max);
    }
    
    Random asRandom() {
	return _notGame;
    }
    
}
