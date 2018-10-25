package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class MonitorGroup {
    
    private final Map<VariableSpecifier, Set<Monitor>> _monitors;
    
    MonitorGroup() {
	_monitors = new HashMap<>();
    }
    
    MonitorGroup(MonitorGroup copyOf) {
	_monitors = new HashMap<>(copyOf._monitors);
    }
    
    void addMonitor(VariableSpecifier vs, Monitor monitor) {
	if(!_monitors.containsKey(vs))
	    _monitors.put(vs, new HashSet<Monitor>());
	_monitors.get(vs).add(monitor);
    }
    
    void pokeMonitors(AbstractContext context, Set<DatumSpecifier> regardingData) throws PlexException {
	for(DatumSpecifier ds : new HashSet<>(regardingData)) {
	    if(!ds.isVirtual())
		regardingData.add(ds.toAnySpecifier());
	}
	for(DatumSpecifier ds : regardingData) {
	    VariableSpecifier vs = ds.toVariableSpecifier(context);
	    if(!_monitors.containsKey(vs)) continue;
	    Set<Monitor> applicable = _monitors.get(vs);
	    for(Monitor monitor : applicable)
		monitor.onPoked(context, ds.getRecord(context));
	}
    }
    
}
