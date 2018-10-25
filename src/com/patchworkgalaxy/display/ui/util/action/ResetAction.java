package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;

public class ResetAction extends Action {

    @Override
    public void act(Component actOn) {
	actOn.reset();
    }
    
}
