package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;

public class RemoveConditionEvent extends GameEvent {
    
    public RemoveConditionEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	//if(!wasCancelled())
	    receiver.removeCondition(props.getString("Subtype"));
    }

    @Override
    protected void cancelImpl() { }
    
    @Override
    protected void cancelImpl2() {}
    
}
