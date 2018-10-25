package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.tile.TileGroupStrategic;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.TemplateRegistry;


public class CaptureEvent extends GameEvent {

    public static GameEvent getCaptureEvent(GameComponent sender, GameComponent receiver) {
	return TemplateRegistry.EVENTS.lookup("Location Staging").instantiate(sender, receiver);
    };
    
    public CaptureEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
	
    @Override
    protected void postImpl() {
       // !wasCancelled() &&
	if( receiver instanceof TileGroupStrategic) {
	    ((TileGroupStrategic)receiver).capture(this);
	}
    }

    @Override
    protected void cancelImpl() {
	
	if(!(receiver instanceof TileGroupStrategic)) {
	    successChance = 0;
	    successModifier = 0;
	}
	
    }
    
    @Override
    protected void cancelImpl2() {}
    
}
