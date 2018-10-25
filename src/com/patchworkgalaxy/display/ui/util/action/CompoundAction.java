package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompoundAction extends Action {
    
    private final List<Action> _actions;

    public CompoundAction() {
	_actions = new ArrayList<>();
    }
    
    public CompoundAction(Action... actions) {
	_actions = new ArrayList<>(Arrays.asList(actions));
    }
    
    public CompoundAction addAction(Action action) {
	_actions.add(action);
	return this;
    }
    
    @Override
    public void act(Component actOn) {
	for(Action action : _actions)
	    action.act(actOn);
    }
    
    
    
}
