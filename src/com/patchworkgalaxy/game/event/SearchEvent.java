package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileCollector;
import com.patchworkgalaxy.game.targetvalidator.TargetValidator;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.parser.CanBeEvent;
import java.util.Set;

public class SearchEvent extends GameEvent {

    private final TargetValidator _validator;
    private float _sum;
    
    public SearchEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
	_validator = TargetValidator.getByName(props.getString("Subtype"));
	virtualAction = isQuery();
    }
    
    @Override
    protected void postImpl() {
	int range = getMagnitude();
	Set<Tile> targets = new TileCollector(receiver.getPosition(), range).getTiles();
	for(Tile tile : targets) {
	    if(tile != receiver.getPosition() && (_validator == null || _validator.validateShot(sender, tile))) {
		Ship s = tile.getShip();
		if(s == null)
		    found(tile);
		else
		    found(s);
	    }
	}
    }
    
    private void found(GameComponent to) {
	for(String eventName : multi) {
	    CanBeEvent template = ((CanBeEvent)(getGameState().lookup(eventName)));
	    GameEvent event = template.toEvent(sender, to, this);
	    if(event instanceof SearchEvent)
		event = template.toEvent(to, to, this);
	    event.enqueue();
	    _sum += event.toFloat(getGameState());
	}
    }
    
    @Override
    protected void postMultiEvents(GameComponent to) {}

    @Override
    protected void cancelImpl() {}
    
    @Override
    protected void cancelImpl2() {}

    private boolean isQuery() {
	for(String eventName : multi) {
	    CanBeEvent template = ((CanBeEvent)(getGameState().lookup(eventName)));
	    GameEvent event = template.toEvent(sender, receiver);
	    if(!event.isVirtual())
		return false;
	}
	return true;
    }
    
    @Override
    public float toFloat(GameState gameState) {
	if(wasPosted())
	    return _sum;
	else if(isQuery()) {
	    virtualize();
	    return _sum;
	}
	else
	    return 0;
    }
    
}
