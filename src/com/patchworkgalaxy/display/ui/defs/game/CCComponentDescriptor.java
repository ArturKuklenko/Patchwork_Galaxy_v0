package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;

class CCComponentDescriptor extends StandardComponentDescriptor {
    
    CCComponentDescriptor(String name, Vector2f center, Vector2f size) {
	super(name, center, size);
	setOpacity(.0f);
	addTransition(.15f, Property.OPACITY);
	addSubscription(Topic.GAME_CHRONO);
    }
    
}
