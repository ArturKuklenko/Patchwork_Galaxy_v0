package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.game.tile.TileGroup;
import com.patchworkgalaxy.game.tile.TileGroupStrategic;
import com.patchworkgalaxy.general.subscriptions.BaseSubscribable;
import com.patchworkgalaxy.general.subscriptions.Subscribable;

class LocationViewerCD extends StandardComponentDescriptor {
    
    private TileGroup _tileGroup;
    private final Subscribable _topic = new BaseSubscribable();
    
    LocationViewerCD() {
	super("Strategic Location",
		new Vector2f(Definitions.LOCATION_PANEL_X, Definitions.RESOURCES_PANEL_Y),
		new Vector2f(Definitions.RESOURCES_PANEL_WIDTH, Definitions.RESOURCES_PANEL_HEIGHT)		
		);
	addCallback(new Action() {
	    @Override public void act(Component actOn) { update(actOn); }
	}.asCallback(ComponentCallback.Type.UPDATE));
	addSubscription(_topic);
	setTextAlignRight();
    }
    
    void update(TileGroup tileGroup) {
	_tileGroup = tileGroup;
	_topic.update();
    }
    
    private ColoredText getDescription() {
	if(_tileGroup instanceof TileGroupStrategic)
	    return ((TileGroupStrategic)_tileGroup).getDescription();
	else
	    return new ColoredText();
    }
    
    private void update(Component actOn) {
	actOn.write(getDescription(), Property.TEXT);
    }
    
}
