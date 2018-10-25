package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.display.ui.controller.Component;

public class CancelAction extends Action {
    
    private final boolean _blurs;
    
    public CancelAction() {
	this(true);
    }
    
    public CancelAction(boolean blurs) {
	_blurs = blurs;
    }

    @Override public void act(Component actOn) {
	if(_blurs)
	    UX2DControl.blurFocusedControl();
	UX2D.getInstance().acceptKeyInput(Definitions.KEY_ESC);
    }
    
}
