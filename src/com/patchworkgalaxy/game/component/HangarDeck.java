package com.patchworkgalaxy.game.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
class HangarDeck {
    
    private final Ship _ship;
    private final List<Hangar> _hangars;
    Hangar _hangar;
    
    private static final Comparator<Ship> _sort = new Comparator<Ship>() {
	@Override public int compare(Ship o1, Ship o2) {
	    int result = o1.getTemplateId() - o2.getTemplateId();
	    if(result == 0) {
		result = o2.getHullIntegrity() - o1.getHullIntegrity();
	    }
	    return result;
	}
	
    };
    
    HangarDeck(Ship ship, String[] hangarData) {
	_ship = ship;
	_hangars = new ArrayList<>();
	if(hangarData != null && hangarData.length > 0)
	    parseHangarData(hangarData);
    }
    Hangar getCurrentHangar (){
        return _hangar;
    }
    boolean canAcceptShip(Ship ship) {
	for(Hangar hangar : _hangars) {
	    if(hangar.canAcceptShip(ship))
		return true;
	}
	return false;
    }

    boolean acceptShip(Ship ship) {
	for(Hangar hangar : _hangars) {
	    if(hangar.acceptShip(ship))
		return true;
	}
	return false;
    }
    
    boolean launchShip(Ship ship) {
	if(!ship.alive()) return false;
	for(Hangar hangar : _hangars) {
	    if(hangar.launchShip(ship))
		return true;
	}
	return false;
    }
    
    void dispatchHangarEvent(GameComponent sender, String eventName, GameEvent cause) {
	for(Hangar hangar : _hangars)
	    hangar.dispatchHangarEvent(sender, eventName, cause);
    }
    
    List<Ship> getShips() {
	List<Ship> result = new ArrayList<>();
	for(Hangar hangar : _hangars)
	    result.addAll(hangar.getShips());
	Collections.sort(result, _sort);
	return result;
    }
    
    
    private void parseHangarData(String[] hangarData) {
	int capacity = 0;
	Set<String> compatibility = new HashSet<>();
	for(String s : hangarData) {
	    int i = getInt(s);
	    if(i > 0) {
		if(!compatibility.isEmpty()) {
		    addHangar(capacity, compatibility);
		    compatibility = new HashSet<>();
		}
		capacity = i;
	    }
	    else
		compatibility.add(s);
	}
	if(!compatibility.isEmpty())
	    addHangar(capacity, compatibility);
    }
    
    private void addHangar(int capacity, Set<String> compatibility) {
        _hangar = new Hangar(capacity, compatibility, _ship);
	_hangars.add(_hangar); 
    }
    
    private int getInt(String s) {
	try {
	    return Integer.valueOf(s);
	}
	catch (NumberFormatException e) {
	    return -1;
	}
    }
    
}
