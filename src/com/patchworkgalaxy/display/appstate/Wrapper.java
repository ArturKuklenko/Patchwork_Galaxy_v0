package com.patchworkgalaxy.display.appstate;

import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.state.ClientGameState;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.GameState;

class Wrapper {
    
    private final GameState _wraps;
    private final Player _localPlayer;
    
    Wrapper(GameHistory history) {
	_wraps = ClientGameState.getCurrentInstance();
	_localPlayer = _wraps.getPlayer(history.getLocalId());
    }
    
    GameState getWrappedGame() {
	return _wraps;
    }
    
    Player getLocalPlayer() {
	return _localPlayer;
    }
    
}
