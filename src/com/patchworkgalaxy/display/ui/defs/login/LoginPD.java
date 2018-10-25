package com.patchworkgalaxy.display.ui.defs.login;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.CreatePanelAction;
import com.patchworkgalaxy.display.ui.util.action.TeardownPanelAction;
import com.patchworkgalaxy.display.ui.util.action.WriteAction;
import java.util.ArrayList;
import java.util.List;

public class LoginPD extends StandardPanelDescriptor {
    
    private static final Vector2f CENTER = Vector2f.ZERO;
    private static final Vector2f SIZE = new Vector2f(.5f, .66f);
    private Panel[] _secondaryPanel = new Panel[1];
    
    public LoginPD() {
	List<ComponentDescriptor> components = new ArrayList<>();
	components.add(getUnderlay());
	components.add(getBackground());
	setComponents(components);
    }
    
    private ComponentDescriptor getUnderlay() {
	return new StandardComponentDescriptor(Vector2f.ZERO, Vector2f.UNIT_XY)
		.setZIndex(Definitions.Z_INDEX_MED)
		.setBackgroundImage("Interface/offblack.png")
		.setOpacity(0f)
		.addTransition(.5f, Property.OPACITY)
		.addCallback(new WriteAction(.75f, Property.OPACITY).asCallback(ComponentCallback.Type.INITIALIZE))
		.addCallback(new TeardownPanelAction().asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.setHotkey(Definitions.KEY_ESC)
		;
    }
    
    private ComponentDescriptor getBackground() {
	return new StandardComponentDescriptor(CENTER, SIZE)
		.setBackgroundImage("Interface/pwgui/window/window_medium.png")
		.setZIndex(Definitions.Z_INDEX_MED + 1)
		.setOpacity(0f)
		.addTransition(1f, Property.OPACITY)
		.addCallback(SECONDARY_PD.asCallback(ComponentCallback.Type.INITIALIZE))
		;
    }
    
    private final Action SECONDARY_PD = new Action() {
	@Override public void act(Component actOn) {
	    Efforts.submit(new WriteAction(1f, Property.OPACITY).asCallable(actOn))
		    .then(new CreatePanelAction(new SecondaryLoginPD(), _secondaryPanel).asCallable(actOn));
	}
    };
    
    @Override public void onHide(Panel panel) {
	if(_secondaryPanel[0] != null)
	    _secondaryPanel[0].hide();
    }
    
}
