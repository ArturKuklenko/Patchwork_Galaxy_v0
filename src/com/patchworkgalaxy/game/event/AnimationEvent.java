package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.general.data.GameProps;

public class AnimationEvent extends GameEvent {
    
    public AnimationEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	//if(!wasCancelled())
	    receiver.getModel().play(props.getString("Subtype"));
    }

    @Override
    protected void cancelImpl() { }
    
    @Override
    protected void cancelImpl2() {}



}
