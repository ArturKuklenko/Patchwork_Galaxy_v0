package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import java.util.ArrayList;
import java.util.List;

class DockedShipComponentDescriptor extends StandardComponentDescriptor {
    
    private static final Vector2f ORIGIN = new Vector2f(.8f, .8f);
    private static final Vector2f DIMENSIONS = new Vector2f(.15f, .05f);
    private static final Vector2f STEP = new Vector2f(0f, -.12f);
    
    static List<ComponentDescriptor> getHangarButtons(GameUIPD gameUI, Ship ship) {
	List<Ship> dockedShips = ship.getDockedShips();
	List<ComponentDescriptor> result = new ArrayList<>(dockedShips.size());
	for(int i = dockedShips.size(); --i >= 0;) {
	    result.add(new DockedShipComponentDescriptor(gameUI, dockedShips.get(i), i));
	}
	return result;
    }
    
    private DockedShipComponentDescriptor(final GameUIPD gameUI, final Ship ship, int index) {
	super(ORIGIN.add(STEP.mult(index)), DIMENSIONS);
	this
		.setBackgroundImage("Interface/pwgui/bars/progress_bar_background.png")
		.setText(ship.getDisplayName() + "\n" +
		    "Hull: " + ship.getHullIntegrity() + "/" + ship.getMaxHullIntegrity() +
		    ((ship.getMaxShieldIntegrity() > 0) ? ("Shield: " + ship.getShieldIntegrity() + "/" + ship.getMaxShieldIntegrity()) : "") +
		    "TB: " + ship.getThermalLimit(true, true)
		)
		.addCallback(new Action() { 
		    @Override public void act(Component actOn) {
			ShipSystem engine = ship.getSystem("engine", true);
			if(engine != null)
			    gameUI.updateCommandCard(ship, engine);
		    }
		}.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		;
	if(ship.getPlayer().isLocal())
	    setTooltip("Launch this ship");
    }
    
}
