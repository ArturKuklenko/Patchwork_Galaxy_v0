package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.TeardownPanelAction;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.template.types.ShipTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PatchSpacePanelDescriptor extends StandardPanelDescriptor {
    
    private static boolean _active;
    
    public PatchSpacePanelDescriptor() {
	List<ComponentDescriptor> descriptors = getDeployShipDescriptors();
	descriptors.add(getReturnButton());
	setComponents(descriptors);
    }
    
    @Override public void onShow(Panel panel) {
	GameAppState.getLocalPlayer().enterPatchSpace();
	_active = true;
    }
    
    @Override public void onHide(Panel panel) {
	GameAppState.getLocalPlayer().exitPatchSpace();
	Efforts.submit(new Callable<Void>() {
	    @Override public Void call() {
		try {
		    Thread.sleep(1000);
		} catch(InterruptedException e) {}
		_active = false;
		return null;
	    }
	});
    }
    
    static boolean isActive() {
	return _active;
    }
    
    private static List<ComponentDescriptor> getDeployShipDescriptors() {
	List<ComponentDescriptor> result = new ArrayList<>();
	int xIndex = 0; int yIndex = 0;
	boolean skipHq = true;
	for(ShipTemplate template : GameAppState.getLocalPlayer().getFaction().getShipTemplates()) {
	    if(skipHq) {
		skipHq = false;
		continue;
	    }
	    result.add(new DeployShipComponentDescriptor(template, xIndex, yIndex));
	    switch(xIndex) {
	    case 0:
		xIndex = -1;
		break;
	    case -1:
		xIndex = 1;
		break;
	    default:
		xIndex = 0;
		++yIndex;
		break;
	    }
	}
	return result;
    }
    
    private static ComponentDescriptor getReturnButton() {
	Vector2f origin = new Vector2f(Definitions.GAME_MENU_BUTTON_ORIGIN);
	origin.x *= -1;
	return new StandardComponentDescriptor(null, origin, Definitions.GAME_MENU_BUTTON_SIZE)
		.setBackgroundImage(Definitions.GAME_MENU_BUTTON_ALT_IMAGE)
		.setZIndex(Definitions.Z_INDEX_ULTRAHIGH + 50)
		.setText(new ColoredText("Return to battle"))
		.setTextAlignCenter()
		.addCallback(new TeardownPanelAction().asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.setHotkey(Definitions.KEY_ESC)
		;
		
    }
    
}
