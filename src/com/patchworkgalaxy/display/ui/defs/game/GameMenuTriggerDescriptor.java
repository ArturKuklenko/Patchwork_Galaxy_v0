package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.general.util.Utils;

class GameMenuTriggerDescriptor extends StandardComponentDescriptor {
    
    private static final int COUNT_BUTTONS = Definitions.GAME_MENU_BUTTON_LABELS.length;
    
    private static final Vector2f
	    SMALL_ORIGIN = Definitions.GAME_MENU_BUTTON_ORIGIN,
	    LARGE_ORIGIN = Utils.averageVecs(SMALL_ORIGIN, getButtonOrigin(1, COUNT_BUTTONS - 1)),
	    SMALL_SIZE = Definitions.GAME_MENU_BUTTON_SIZE,
	    LARGE_SIZE = new Vector2f(SMALL_SIZE.x * 2, SMALL_SIZE.y * COUNT_BUTTONS);
	    ;
    
    GameMenuTriggerDescriptor() {
	super(null,
		Definitions.GAME_MENU_BUTTON_ORIGIN,
		Definitions.GAME_MENU_BUTTON_SIZE
		);
	this
		.addCallback(SHOW_GAME_MENU_BUTTONS.asCallback(ComponentCallback.Type.MOUSE_IN))
		.addCallback(HIDE_GAME_MENU_BUTTONS.asCallback(ComponentCallback.Type.MOUSE_OUT))
		.addTransition(.5f, Property.CENTER, Property.SIZE)
		;
    }
    
    static Vector2f getButtonOrigin(int yIndex) {
	return getButtonOrigin(0, yIndex);
    }
    
    static Vector2f getButtonOrigin(int xIndex, int yIndex) {
	    Vector2f result = new Vector2f(Definitions.GAME_MENU_BUTTON_ORIGIN);
	    float xOffset = 2 * Definitions.GAME_MENU_BUTTON_SIZE.x * xIndex;
	    float yOffset = -2 * Definitions.GAME_MENU_BUTTON_SIZE.y * yIndex;
	    result.x += xOffset;
	    result.y += yOffset;
	    return result;
    }
    
    static void showGameMenuButtons(Component actOn) {
	boolean skip = true;
	Vector2f destination = new Vector2f(Definitions.GAME_MENU_BUTTON_ORIGIN);
	for(String label : Definitions.GAME_MENU_BUTTON_LABELS) {
	    Panel panel = actOn.getPanel();
	    if(skip) {
		skip = false;
		continue;
	    }
	    Component component = panel.getComponent(label);
	    if(component == null) continue;
	    destination.addLocal(Definitions.GAME_MENU_BUTTON_STEP);
	    component.write(destination, Property.CENTER);
	    component.write(.8f, Property.OPACITY);
	}
	actOn.write(LARGE_ORIGIN, Property.CENTER);
	actOn.write(LARGE_SIZE, Property.SIZE);
    }
    
    static void hideGameMenuButtons(Component actOn) {
	boolean skip = true;
	Panel panel = actOn.getPanel();
	for(String label : Definitions.GAME_MENU_BUTTON_LABELS) {
	    if(skip) {
		skip = false;
		continue;
	    }
	    Component component = panel.getComponent(label);
	    if(component == null) continue;
	    component.write(Definitions.GAME_MENU_BUTTON_ORIGIN, Property.CENTER);
	    component.write(0f, Property.OPACITY);
	}
	panel.getComponent("Confirm Concede").reset();
	actOn.write(SMALL_ORIGIN, Property.CENTER);
	actOn.write(SMALL_SIZE, Property.SIZE);	
    }
    
    private static final Action HIDE_GAME_MENU_BUTTONS = new Action() {
	@Override
	public void act(Component actOn) {
	    hideGameMenuButtons(actOn);
	}
    };
    
    private static final Action SHOW_GAME_MENU_BUTTONS = new Action() {
	@Override
	public void act(Component actOn) {
	    showGameMenuButtons(actOn);
	}
    };
    
}
