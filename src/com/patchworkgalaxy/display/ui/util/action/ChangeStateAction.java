package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.descriptors.ComponentState;
import com.patchworkgalaxy.display.ui.controller.Component;

public class ChangeStateAction extends Action {
    
    private final ComponentState _state;
    
    public ChangeStateAction(ComponentState state) {
	_state = state;
    }

    @Override
    public void act(Component actOn) {
	actOn.write(_state);
    }
    
}
