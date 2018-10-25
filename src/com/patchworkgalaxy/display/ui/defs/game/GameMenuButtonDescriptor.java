package com.patchworkgalaxy.display.ui.defs.game;

import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.VirtualKeyboard;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.NoOpAction;
import com.patchworkgalaxy.game.component.Player;

class GameMenuButtonDescriptor extends StandardComponentDescriptor {
    
    GameMenuButtonDescriptor(int index) {
	super(Definitions.GAME_MENU_BUTTON_LABELS[index],
		Definitions.GAME_MENU_BUTTON_ORIGIN,
		Definitions.GAME_MENU_BUTTON_SIZE
		);
	this
		.addCallback(getActionForIndex(index).asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.addTransition(.5f, Property.CENTER, Property.OPACITY)
		.setText(new ColoredText(Definitions.GAME_MENU_BUTTON_LABELS[index]))
		.setTextAlignCenter()
		.setTextSize(16f)
		.setZIndex(Definitions.Z_INDEX_HIGH - (index + 1))
		.setBackgroundImage(Definitions.GAME_MENU_BUTTON_BACKGROUND_IMAGE)
		;
	if(index == 0)
	    setHotkey(VirtualKeyboard.TAB);
	else
	    setOpacity(0f);
    }
    
    private static Action getActionForIndex(int index) {
	if(index >= 0 && index < GAME_MENU_ACTIONS.length)
	    return GAME_MENU_ACTIONS[index];
	return new NoOpAction();
    }
    
    private static final Action[] GAME_MENU_ACTIONS = new Action[] {
	new Action() {
	    @Override public void act(Component actOn) {
		GameAppState.getLocalPlayer().pass();
	    }
	},
	new Action() {
	    @Override public void act(Component actOn) {
		Component confirm = actOn.getPanel().getComponent("Confirm Concede");
		confirm.write(GameMenuTriggerDescriptor.getButtonOrigin(1, 1), Property.CENTER);
		confirm.write(1f, Property.OPACITY);
	    }
	},
	new Action() {
	    @Override public void act(Component actOn) {
		//transitional
		UX2D.getInstance().getControl("Game Chat Input").focus();
	    }
	}
    };
    
}
