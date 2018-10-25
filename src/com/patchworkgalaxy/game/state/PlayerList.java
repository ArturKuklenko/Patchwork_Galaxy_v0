package com.patchworkgalaxy.game.state;

import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class PlayerList {
    
    private final GameState _gameState;
    
    private final Player[] _players;
    private Set<Player> _livePlayers;
    private int _currentPlayerId;
    private Player _winner;
    
    private static final GameProps DUMMY_PLAYER_PROPS;
    static {
	DUMMY_PLAYER_PROPS = new MutableGameProps()
		.set("Name", "Observer")
		.set("Faction", TemplateRegistry.FACTIONS.lookup(0).getName())
		.immutable();
    }
    
    PlayerList(GameState gameState, int startingPlayer, GameProps... playerdefs) {
	_gameState = gameState;
	_currentPlayerId = startingPlayer;
	_players = new Player[playerdefs.length];
	for(int i = _players.length; --i >= 0;)
	    _players[i] = new Player(playerdefs[i], gameState, i);
	_livePlayers = new HashSet<>();
	_livePlayers.addAll(Arrays.asList(_players));
    }
    
    Player getPlayer(int id) {
	if(id >= 0 && id < _players.length)
	    return _players[id];
	else
	    return new Player(DUMMY_PLAYER_PROPS, _gameState, id);
    }
    
    private int getNextId(int id) {
	return (id + 1) % _players.length;
    }
    
    private int getNextLiveId(int id) {
	int id2 = getNextId(id);
	while(id != id2 && !_players[id2].alive())
	    id2 = getNextId(id2);
	return id2;
    }
    
    private Player getClosestLivePlayer(int id) {
	if(_players[id].alive())
	    return _players[id];
	else
	    return _players[getNextLiveId(id)];
    }
    
    Player getCurrentPlayer() {
	return getClosestLivePlayer(_currentPlayerId);
    }
    
    Player getNextPlayer() {
	return _players[getNextLiveId(_currentPlayerId)];
    }
    
    Player getEnemyOf(Player player) {
	return _players[getNextId(player.getId())];
    }
    
    void advanceCurrentPlayer() {
	_currentPlayerId = getNextLiveId(_currentPlayerId);
    }

    Player getWinner() {
	return _winner;
    }
    
    Player[] getRawPlayers() {
	return _players.clone();
    }
    
    void update() {
        
        //adolf START
        Set<Player> notAlive = new HashSet<>();
        for(Player player : _livePlayers) {
	    if(_livePlayers.contains(player) && !player.alive())
                notAlive.add(player);
	    if(_livePlayers.size() == 1)
		_winner = _livePlayers.iterator().next();
        }
        
        if (!notAlive.isEmpty()) {
          for (Player player : notAlive) {
              _livePlayers.remove(player);
          }
        }
        
        //adolf END
        
    }
    
    int size() {
	return _players.length;
    }
    
}
