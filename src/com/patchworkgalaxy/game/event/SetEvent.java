package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;

public class SetEvent extends GameEvent {
    
    public SetEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() { }

    @Override
    protected void cancelImpl() { }
    
    @Override
    protected void cancelImpl2() {}
    
}
