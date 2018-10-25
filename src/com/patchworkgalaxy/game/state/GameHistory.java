package com.patchworkgalaxy.game.state;

import com.jme3.network.Message;
import com.jme3.network.serializing.Serializable;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Serializable
public class GameHistory implements Message, Iterable<Evolution> {
    
    private static final long serialVersionUID = 1L;
    private int _thisPlayerId;
    
    private String[] _playerNames;
    private String[] _playerFactions;
    
    private List<Evolution> _evolutions;
    
    /**
     * 
     * @deprecated Serialization only 
     */
    @Deprecated public GameHistory() {
	this(new String[]{}, new String[]{}, 0);
    }
    
    public GameHistory(String[] playerNames, String[] playerFactions, int thisPlayerId) {
	if(playerNames.length != playerFactions.length)
	    throw new IllegalArgumentException("Must have exactly one faction per player");
        thisPlayerId = _thisPlayerId;
	_playerNames = playerNames;
	_playerFactions = playerFactions;
	_evolutions = new ArrayList<>();
    }
    
    /**
     * Copy constructor.
     * @param copyOf 
     */
    public GameHistory(GameHistory copyOf) {
	_evolutions = new ArrayList<>(copyOf._evolutions);
	_playerNames = copyOf._playerNames;
	_playerFactions = copyOf._playerFactions;
	_thisPlayerId = copyOf._thisPlayerId;
    }
    
    public GameProps[] getPlayers() {
	GameProps[] result = new GameProps[_playerNames.length];
	for(int i = result.length; --i >=0;)
	    result[i] = new MutableGameProps()
		    .set("Name", _playerNames[i])
		    .set("Faction", _playerFactions[i])
		    .immutable();
	return result;
    }

    @Override
    public Message setReliable(boolean f) {
	//pwg in-game messages are always reliable
	return this;
    }

    @Override
    public boolean isReliable() {
	return true;
    }
    
    public int getLocalId() {
	return _thisPlayerId;
    }
    
    public GameHistory withLocalId(int id) {
	GameHistory result = new GameHistory(this);
	result._thisPlayerId = id;
	return result;
    }

    public void add(Evolution evolution) {
	_evolutions.add(evolution);
    }
    
    @Override
    public Iterator<Evolution> iterator() {
	return _evolutions.iterator();
    }
    
}
