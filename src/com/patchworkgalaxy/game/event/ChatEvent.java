package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.network.server.ChatMessage;

public class ChatEvent extends GameEvent implements GameEvent.AlwaysVirtual {

    public ChatEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	if(Player.isLocal(receiver.getPlayer())) {
	    ChatMessage message = new ChatMessage(props.getString("Subtype"));
	    message.suppressColon();
	    ClientManager.client().send(message);
	}
    }

    @Override
    protected void cancelImpl() {}

    @Override
    protected void cancelImpl2() {}
    
}
