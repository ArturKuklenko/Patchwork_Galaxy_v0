package com.patchworkgalaxy.game.component;

import com.patchworkgalaxy.game.misc.Formula;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Resolver;
import com.patchworkgalaxy.template.parser.CanBeEvent;

public class Condition extends GameComponent {
    
    private final GameComponent _attachedTo;
    private int _duration;
    private final Resolver _turnStartEvent, _turnEndEvent, _reactEvent;
    private final Resolver _formula;
    private final Resolver _magnitude, _success;
    private final Resolver _reactTarget;
    private final boolean _outgoing;
    private final int _trigger;
    
    public Condition(GameProps props, GameComponent attachedTo) {
	super(props, Breed.CONDITION);
	_attachedTo = attachedTo;
	int duration = props.getInt("Duration");
	if(duration > 0)
	    _duration = duration;
	else
	    _duration = Integer.MAX_VALUE;
	
	_magnitude = Resolver.createResolver(getGameState(), props.getString("Magnitude"), true);
	_success = Resolver.createResolver(getGameState(), props.getString("Success"), true);
	_outgoing = props.getBoolean("Outgoing!");
	_trigger = props.getInt("Trigger");
	
	_formula = Resolver.createResolver(getGameState(), props.getString("Formula"), true);
	_turnStartEvent = Resolver.createResolver(getGameState(), props.getString("TurnStart"), true);
	_turnEndEvent = Resolver.createResolver(getGameState(), props.getString("TurnEnd"), true);
	_reactEvent = Resolver.createResolver(getGameState(), props.getString("Reaction"), true);
	_reactTarget = Resolver.createResolver(getGameState(), props.getString("ReactTarget"), false);
    }
    
    GameComponent getAttachedTo() {
	return _attachedTo;
    }

    @Override
    public Tile getPosition() {
	return _attachedTo.getPosition();
    }
    
    public int getDuration() {
	return _duration;
    }
    
    @Override
    public void turnStart() {
	super.turnStart();
	if(_duration > 0)
	    --_duration;
	if(_duration != 0) {
	    turnStartEvent();
	}
	else
	    kill();
    }
    
    @Override
    public void turnEnd() {
	super.turnEnd();
	turnEndEvent();
    }
    
    private void turnStartEvent() {
	getGameState().setNamespace("~condition", this);
	if(_formula == null || _formula.resolve(Formula.class).check(getGameState())) {
	    if(_turnStartEvent != null)
		_turnStartEvent.resolve(CanBeEvent.class).toEvent(_attachedTo, _attachedTo).enqueue();
	}
    }
    
    private void turnEndEvent() {
	getGameState().setNamespace("~condition", this);
	if(_formula == null || _formula.resolve(Formula.class).check(getGameState())) {
	    if(_turnEndEvent != null)
		_turnEndEvent.resolve(CanBeEvent.class).toEvent(_attachedTo, _attachedTo).enqueue();
	}
    }
    
    @Override
    public boolean checkEvent(GameEvent gameEvent, boolean outgoing) {
	
	getGameState().setNamespace("~condition", this);
	
	//incoming conditions don't apply to outgoing events and vice verse
	if(_outgoing != outgoing)
	    return false;
	
	//we only apply if our trigger and the event have a flag in common
	//(or if we have no trigger in the first place)
	if(_trigger != 0 && (gameEvent.getFlags() & _trigger) == 0)
	    return false;
	
	//if we have more precise use conditions, check them too
	if(_formula != null && !_formula.resolve(Formula.class).check(getGameState()))
	    return false;
	
	int magnitude = (_magnitude == null ? 0 : (int)_magnitude.toFloat(getGameState()));
	float success = (_success == null ? 0 : _success.toFloat(getGameState()));
	
	gameEvent.modify(magnitude, success);
	if(!gameEvent.isVirtual())
	    react();
	return true;
    }
    
    private void react() {
	if(_reactEvent != null) {
	    CanBeEvent event = _reactEvent.resolve(CanBeEvent.class);
	    GameComponent target = _reactTarget == null ? _attachedTo : _reactTarget.resolve(GameComponent.class);
	    if(target != null && target.alive())
		event.toEvent(this, target).enqueue();
	}
    }

    @Override
    public Object lookup(String name) {
	switch (name) {
	case "attachment":
	    return _attachedTo;
	case "duration":
	    return _duration;
	default:
	    return super.lookup(name);
	}
    }

    @Override
    public final GameState getGameState() {
	return _attachedTo.getGameState();
    }
    
    @Override
    public Player getPlayer() {
	return _attachedTo.getPlayer();
    }
    
    public boolean isGlobal() {
	return props.getBoolean("Global!");
    }
    
    @Override
    public boolean alive() {
	return super.alive() && _attachedTo.alive();
    }
    
    @Override
    public void onJoinGame() {
	super.onJoinGame();
	if(isGlobal())
	    getGameState().addGlobalCondition(this);
    }

    @Override
    public double getAccuracyFalloff() {
       return _attachedTo.getAccuracyFalloff();
    }
    
}
