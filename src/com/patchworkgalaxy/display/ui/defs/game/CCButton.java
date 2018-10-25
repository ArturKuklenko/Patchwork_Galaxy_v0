package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.game.commandcard.CommandCardEntry;
import com.patchworkgalaxy.game.component.ShipSystem;

class CCButton extends CCComponentDescriptor {
    
    static final float WIDTH = Definitions.COMMAND_CARD_BUTTON_WIDTH;
    static final float HEIGHT = Definitions.COMMAND_CARD_BUTTON_HEIGHT;
    static final Vector2f SIZE = new Vector2f(WIDTH, HEIGHT);
    
    static final float FULL_WIDTH = 2 * (Definitions.COMMAND_CARD_BUTTON_PADDING_X + WIDTH);
    static final float FULL_HEIGHT = 2 * (Definitions.COMMAND_CARD_BUTTON_PADDING_Y + HEIGHT);
    
    private final CommandCardEntry _entry;
    
    CCButton(final GameUIPD gameUI, final CommandCardEntry entry, Vector2f center) {
	super(entry.getDisplayName(), center, SIZE);
	_entry = entry;
	Action fade = new Action() { @Override public void act(Component actOn) {
	    actOn.write(entry.canFire() ? .75f : .35f, Property.OPACITY);
	} };
	Action clicked = new Action() {
	    @Override public void act(Component actOn) {
		ShipSystem system = entry.getSystem();
		if(system == null || !entry.canFire()) return;
		if(system.autotarget()) return;
		gameUI.updateCommandCard(system.getShip(), system);
	    }
	};
	this.
		setBackgroundImage(entry.getIcon())
		.setZIndex(Definitions.Z_INDEX_HIGH + 50)
		.setTooltipDescriptor(new CCButtonTooltipDescriptor(_entry))
		.addCallback(clicked.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.addCallback(fade.asCallback(ComponentCallback.Type.UPDATE))
		;
	if(!entry.canFire())
	    setOpacity(.5f);
	String hotkey = entry.getHotkey();
	if(hotkey != null && !hotkey.isEmpty())
	    setHotkey(hotkey.charAt(0));
    }
    
}
