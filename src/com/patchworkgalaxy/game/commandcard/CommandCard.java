package com.patchworkgalaxy.game.commandcard;

import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import java.util.ArrayList;
import java.util.List;

public class CommandCard {
    
    private final Ship _ship;
    private final CommandCardEntries _entries;
    
    public CommandCard(Ship ship) {
	_ship = ship;
	_entries = new CommandCardEntries(ship);
    }
    
    public String getDisplayName() {
	return _ship.getDisplayName();
    }
    
    public boolean isFriendly() {
	return Player.isLocal(_ship.getPlayer());
    }
    
    public int getMaxHull() {
	return _ship.getMaxHullIntegrity();
    }
    
    public int getCurrentHull() {
	return _ship.getHullIntegrity();
    }
    
    public int getMaxShield() {
	return _ship.getMaxShieldIntegrity();
    }
    
    public int getCurrentShield() {
	return _ship.getShieldIntegrity();
    }
    
    public float getHullPct() {
	int max = getMaxHull();
	if(max == 0) return 0;
	return (float)getCurrentHull() / (float)max;
    }
    
    public float getShieldPct() {
	int max = getMaxShield();
	if(max == 0) return 0;
	return (float)getCurrentShield() / (float)max;
    }
    
    public int countEntries() {
	return _entries.getSize();
    }
    
    public Iterable<CommandCardEntry> getCommandCardEntries() {
	return _entries;
    }
    
    public int countThermalBlocks() {
	return _ship.getThermalLimit(true, true);
    }
    
    public Iterable<ThermalBlockType> getThermalBlocks() {
	List<ThermalBlockType> result = new ArrayList<>();
	int generic = _ship.getThermalLimit(false, false);
	int weapon = _ship.getThermalLimit(true, false) - generic;
	int engine = _ship.getThermalLimit(false, true) - generic;
	for(int i = 0; i < generic; ++i)
	    result.add(ThermalBlockType.GENERIC);
	for(int i = 0; i < weapon; ++i)
	    result.add(ThermalBlockType.WEAPON);
	for(int i = 0; i < engine; ++i)
	    result.add(ThermalBlockType.ENGINE);
	return result;
    }
    
}