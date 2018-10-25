package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;

public class UpdateAllComponentsAction extends Action {

    private final Panel _panel;
    
    public UpdateAllComponentsAction(Panel panel) {
	_panel = panel;
    }
    
    public UpdateAllComponentsAction() {
	_panel = null;
    }
    
    @Override public void act(Component actOn) {
	Panel panel = _panel == null ? actOn.getPanel() : _panel;
	for(Component component : panel.getComponents())
	    component.update(null, null);
    }
    
}
