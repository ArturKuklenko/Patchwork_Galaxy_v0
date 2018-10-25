package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OtherComponentActions extends Action {
    
    private final Map<String, List<Action>> _actions;
    
    public OtherComponentActions() {
	_actions = new HashMap<>();
    }
    
    public OtherComponentActions setRemoteAction(String panelId, Action action) {
	if(!_actions.containsKey(panelId))
	    _actions.put(panelId, new ArrayList<Action>());
	_actions.get(panelId).add(action);
	return this;
    }

    @Override
    public void act(Component actOn) {
	Panel panel = actOn.getPanel();
	for(Entry<String, List<Action>> i : _actions.entrySet()) {
	    for(Action action : i.getValue())
		action.act(panel.getComponent(i.getKey()));
	}
    }
    
}
