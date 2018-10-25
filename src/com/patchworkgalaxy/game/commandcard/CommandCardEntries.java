package com.patchworkgalaxy.game.commandcard;

import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandCardEntries implements Iterable<CommandCardEntry> {
    
    private final List<CommandCardEntry> _entries;
    
    CommandCardEntries(Ship ship) {
	List<ShipSystem> uniqueSystems = ship.getUniqueSystems();
	_entries = new ArrayList<>();
	for(ShipSystem system : uniqueSystems)
	    _entries.add(new CommandCardEntry(system));
    }

    public int getSize() {
	return _entries.size();
    }
    
    @Override
    public Iterator<CommandCardEntry> iterator() {
	return _entries.iterator();
    }
    
}
