package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.display.ui.PanelDescriptorFactory;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.general.data.GameProps;

public class UIEvent extends GameEvent {
    
    public UIEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }

    @Override
    protected void postImpl() {
	if(Player.isLocal(receiver.getPlayer()))
	    PanelDescriptorFactory.showPanelDescriptor(props.getString("Subtype"));
    }

    @Override
    protected void cancelImpl() {}

    @Override
    protected void cancelImpl2() {}
    
}
