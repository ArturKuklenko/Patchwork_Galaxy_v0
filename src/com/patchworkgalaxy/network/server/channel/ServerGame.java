package com.patchworkgalaxy.network.server.channel;

import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.game.state.evolution.EvolutionAcceptor;
import com.patchworkgalaxy.game.state.evolution.EvolutionBuilder;
import com.patchworkgalaxy.game.state.evolution.EvolutionException;
import com.patchworkgalaxy.game.state.evolution.Opcode;
import com.patchworkgalaxy.network.server.account.Account;
import com.patchworkgalaxy.udat.SpecialKeys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ServerGame {
    
    private GameState _gameState;
    private final Map<Account, Integer> _playerIds;
    
    ServerGame() {
	_playerIds = new HashMap<>();
    }
    
    GameHistory start(List<Account> players) {
	String[] names = new String[players.size()];
	String[] factions = new String[players.size()];
	for(int i = players.size(); --i >= 0;) {
	    names[i] = players.get(i).getUsername();
	    factions[i] = players.get(i).getUserDatum(SpecialKeys.FACTION);
	    _playerIds.put(players.get(i), i);
	}
	GameHistory history = new GameHistory(names, factions, 0);
	history.add(new EvolutionBuilder(Opcode.SPECIAL, (byte) 255).getEvolution());
	_gameState = new GameState(history, EvolutionAcceptor.Type.SERVER, false);
	return history;
    }
    
    boolean started() {
	return _gameState != null;
    }
    
    boolean ended() {
	return started() && _gameState.getWinner() != null;
    }
    
    boolean tryEvolution(Evolution evolution) {
	if(!started())
	    return false;
	try {
	    _gameState.evolve(evolution);
	    return true;
	}
	catch(EvolutionException e) {
	    return false;
	}
    }
    
    Player getPlayer(Account account) {
	if(!_playerIds.containsKey(account))
	    throw new IllegalArgumentException();
	return _gameState.getPlayer(_playerIds.get(account));
    }
    
}
