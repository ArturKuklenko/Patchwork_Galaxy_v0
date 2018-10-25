package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.ResetAction;
import com.patchworkgalaxy.display.ui.util.action.UpdateAllComponentsAction;

class ToggleMenuHeaderActions extends Action {
    
    private final PanelDescriptor _pd;
    private Panel _panel;
    
    ToggleMenuHeaderActions(int index) {
	_pd = new MainMenuEntriesPD(index);
    }

    @Override public void act(Component actOn) {
	if(_panel == null) {
	    _panel = UI.Instance.showPanel(_pd);
	    actOn.write(Vector2f.ZERO, Property.CENTER);
	}
	else {
	    new UpdateAllComponentsAction(_panel).act(actOn);
	    _panel = null;
	    new ResetAction().act(actOn);
	}
    }
    
}
