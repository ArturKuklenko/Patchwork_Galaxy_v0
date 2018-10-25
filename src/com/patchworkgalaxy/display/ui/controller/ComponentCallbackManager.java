package com.patchworkgalaxy.display.ui.controller;

import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ComponentCallbackManager {   
    private ComponentCallbackManager() {}
    
    static void setupCallbacks(Component component, UX2DControl oldControl, Set<ComponentCallback> callbacks) {
	Map<CallbackType, Set<ComponentCallback>> map = new EnumMap<>(CallbackType.class);
	for(CallbackType type : CallbackType.values())
	    map.put(type, new HashSet<ComponentCallback>());
	for(ComponentCallback callback : callbacks) {
	    while(callback != null) {
		map.get(callback.getType().getOldType()).add(callback);
		callback = callback.getSecondaryCallback();
	    }
	}
	for(CallbackType type : CallbackType.values()) {
	    Set<ComponentCallback> set = map.get(type);
	    if(!set.isEmpty())
		oldControl.setCallback(type, new Bridge(component, set));
	}
    }
    
    private static class Bridge implements UICallback {
	
	private final Component _component;
	private final List<ComponentCallback> _callbacks;
	
	Bridge(Component component, Collection<ComponentCallback> callbacks) {
	    _component = component;
	    _callbacks = new ArrayList<>(callbacks);
	}

	@Override
	public void callback(UX2DControl control) {
	    for(ComponentCallback callback : _callbacks)
		callback.callback(_component);
	}
	
    }
    
}
