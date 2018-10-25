package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.ui.controller.Component;

public class HotkeyAction extends Action {
    
    private final char _key;
    
    public HotkeyAction(char key) {
	_key = key;
    }

    @Override public void act(Component actOn) {
	UX2D.getInstance().acceptKeyInput(_key);
    }
    
}
