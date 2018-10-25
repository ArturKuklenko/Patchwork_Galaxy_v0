package com.patchworkgalaxy.template.types;

import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Numeric;
import com.patchworkgalaxy.template.Template;
import com.patchworkgalaxy.template.parser.CanBeEvent;

public class EventTemplate extends Template<GameEvent> implements CanBeEvent, Numeric {

    public EventTemplate(GameProps props) {
	super(props, Breed.EVENT.name());
    }

    @Override
    public GameEvent instantiate(Object... params) {
	GameComponent sender = (GameComponent)params[0];
	GameComponent receiver = (GameComponent)params[1];
	GameEvent cause = params.length > 2 ? (GameEvent)params[2] : null;
	GameEvent event = GameEvent.create(props, sender, receiver, cause);
	return event;
    }

    @Override
    public GameEvent toEvent(Object... params) {
	return instantiate(params);
    }

    @Override
    public float toFloat(GameState gameState) {
	
	Object os = gameState.lookup("~event:sender");
	Object or = gameState.lookup("~event:target");
	Object oe = gameState.lookup("~event");
	
	if(!(os instanceof GameComponent && or instanceof GameComponent && oe instanceof GameEvent))
	    return 0;
	
	GameEvent event = instantiate((GameComponent)os, (GameComponent)or, (GameEvent)oe);
	if(event.isVirtual())
	    event.enqueue();
	return event.toFloat(gameState);
	
    }
    
    
    
}
