package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.general.data.GameProps;

public class IterateHangarEvent extends GameEvent {
    
    public IterateHangarEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }

    @Override protected void postImpl() {
	if(receiver instanceof Ship) {
	    for(String eventName : multi)
		((Ship)receiver).dispatchHangarEvent(sender, eventName, this);
	}
    }

    @Override protected void cancelImpl() {}

    @Override protected void cancelImpl2() {}
    
    @Override protected void postMultiEvents(GameComponent to) {}
    
}
