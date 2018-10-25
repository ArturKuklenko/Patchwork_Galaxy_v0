package com.patchworkgalaxy.game.component;

import com.patchworkgalaxy.game.event.AnimationEvent;
import com.patchworkgalaxy.game.event.ApplyConditionEvent;
import com.patchworkgalaxy.game.event.CaptureEvent;
import com.patchworkgalaxy.game.event.ChatEvent;
import com.patchworkgalaxy.game.event.IterateHangarEvent;
import com.patchworkgalaxy.game.event.ModifyVitalEvent;
import com.patchworkgalaxy.game.event.MoveEvent;
import com.patchworkgalaxy.game.event.QueryEvent;
import com.patchworkgalaxy.game.event.RemoveConditionEvent;
import com.patchworkgalaxy.game.event.SearchEvent;
import com.patchworkgalaxy.game.event.SetEvent;
import com.patchworkgalaxy.game.event.UIEvent;
import com.patchworkgalaxy.game.event.WeaponEvent;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Numeric;
import com.patchworkgalaxy.general.data.Resolver;
import com.patchworkgalaxy.template.parser.CanBeEvent;
import java.util.ArrayList;
import java.util.List;

    public abstract class GameEvent extends GameComponent implements Numeric {
    
    private int _magnitude;
    private final Resolver _initialMagnitude, _initialSuccess;
    private final GameEvent _cause;
    int magnitudeModifier;
    protected GameComponent sender, receiver;
    boolean virtual;
    protected boolean virtualAction;
    boolean posted;
    boolean wasCancelled;
    private int _priority;
    
    private final boolean _reflexive;
    
    protected double successChance, successModifier, fallPerTile;
    protected final List<String> multi;
    
    private final boolean _useDirectTargeting;
    
    public static interface AlwaysVirtual {}
    
    protected GameEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, Breed.EVENT);
	this.sender = sender;
	this.receiver = receiver;
	multi = new ArrayList<>();
	_initialMagnitude = Resolver.createResolver(getGameState(), props.getString("Magnitude"), true);
	_initialSuccess = Resolver.createResolver(getGameState(), props.getString("HitChance"), true);
	_cause = cause;
	_reflexive = props.getBoolean("Reflexive!");
	_useDirectTargeting = useDirectTargeting();
	_priority = props.getInt("Priority");
	virtual = virtualAction = this instanceof AlwaysVirtual;
	String multiS = props.getString("Multi");
	if(multiS != null) {
	    for(String event : multiS.split(","))
		multi.add(event.trim());
	}
    }
    
    private boolean useDirectTargeting() {
	return props.getBoolean("DirectTarget!") || this instanceof MoveEvent || this instanceof CaptureEvent;
    }
    
    public static GameEvent create(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	GameEvent event;
	String type = props.getString("Type");
	switch(type) {
	case "SetEvent":
	    event = new SetEvent(props, sender, receiver, cause);
	    break;
	case "MoveEvent":
	    event = new MoveEvent(props, sender, receiver, cause);
	    break;
	case "ModifyVitalEvent":
	    event = new ModifyVitalEvent(props, sender, receiver, cause);
	    break;
	case "ApplyConditionEvent":
	    event = new ApplyConditionEvent(props, sender, receiver, cause);
	    break;
	case "RemoveConditionEvent":
	    event = new RemoveConditionEvent(props, sender, receiver, cause);
	    break;
	case "CaptureEvent":
	    event = new CaptureEvent(props, sender, receiver, cause);
	    break;
	case "SearchEvent":
	    event = new SearchEvent(props, sender, receiver, cause);
	    break;
	case "WeaponEvent":
	    event = new WeaponEvent(props, sender, receiver, cause);
	    break;
	case "AnimationEvent":
	    event = new AnimationEvent(props, sender, receiver, cause);
	    break;
	case "ChatEvent":
	    event = new ChatEvent(props, sender, receiver, cause);
	    break;
	case "QueryEvent":
	    event = new QueryEvent(props, sender, receiver, cause);
	    break;
	case "UIEvent":
	    event = new UIEvent(props, sender, receiver, cause);
	    break;
	case "IterateHangarEvent":
	    event = new IterateHangarEvent(props, sender, receiver, cause);
	    break;
	default:
	    throw new IllegalArgumentException("Bad event type " + type);
	}
	return event;
    }
    
    final void modify(int addMagnitude, float addSuccess) {
        magnitudeModifier += addMagnitude;
        successModifier += addSuccess;
    }
    
    public final void enqueue() {
	if(!posted) {
	    posted = true;
	    getGameState().enqueueEvent(this);
	}
    }
    
    public final void enqueueAtPriority(int priority) {
	_priority = priority;
	enqueue();
    }
    
    private GameComponent indirectTargeting(GameComponent component) {
	if(component == null)
	    return component;
	Tile tile = component.getPosition();
	if(tile == null)
	    return component;
	Ship ship = tile.getShip();
	if(ship == null)
	    return component;
	return ship;
    }
    
    public final void process() {
	
	if(_reflexive)
	    receiver = sender;
	
	if(!_useDirectTargeting)
	    receiver = indirectTargeting(receiver);
	
	getGameState().setNamespace("~event", this);
	getGameState().setNamespace("~ship", sender);
	getGameState().setNamespace("~target", receiver);
	if(_initialMagnitude != null)
	    _magnitude = (int)_initialMagnitude.toFloat(getGameState());

	if(_initialSuccess != null)
	    successChance = _initialSuccess.toFloat(getGameState());
	else
	    successChance = 1;

	    fallPerTile = getAccuracyFalloff();	
        cancelImpl();
        receiver.checkIncomingEvent(this);
	sender.checkOutgoingEvent(this);
        double rng = getGameState().roll(this);
	_magnitude += magnitudeModifier;
	successChance += successModifier;
	cancelImpl2();
        if(successChance < 1 && (successChance <= 0 || (rng > successChance)))
            wasCancelled = true;
       // wasCancelled = false;
	//System.out.println((virtual ? "virtual " : "") + getName() + "[" + getClass().getSimpleName() + "] with flags " + getFlags()  + " had success chance " + successChance + " and magnitude " + _magnitude + ". was it canceled? " + wasCancelled);
	successModifier = 0;
	magnitudeModifier = 0;
	if(!virtual || virtualAction)
	    postImpl();
	if(!wasCancelled && !virtual)
	    postMultiEvents(receiver);
    }
    
    protected void postMultiEvents(GameComponent to) {
	for(String eventName : multi) {
	    CanBeEvent template = ((CanBeEvent)(getGameState().lookup(eventName)));
	    GameEvent event = template.toEvent(sender, to, this);
	    event.enqueueAtPriority(_priority);
	}
    }
    
    protected GameEvent virtualize() {
	virtual = true;
	enqueue();
	return this;
    }
    
    public final boolean isVirtual() {
	return virtual;
    }
 
    public final double getFalloffPerTile() {
	return fallPerTile;
    }
    
    public final double getSuccessChance() {
	return successChance + successModifier;
    }
    
    protected abstract void postImpl();
    
    protected abstract void cancelImpl();
    
    protected abstract void cancelImpl2();
    
    public final boolean wasCancelled() {
        return wasCancelled;
    }
    
    public final int getMagnitude() {
        return  _magnitude + magnitudeModifier;
    }

    public final int getfinalDamage() {
        
        double finalDamage;
        double duration;
        duration = Math.sqrt(Math.pow(Math.abs(getSenderPosition().x-getReceiverPosition().x), 2)+Math.pow(Math.abs(getSenderPosition().y-getReceiverPosition().y), 2));
        finalDamage= Math.pow(1- fallPerTile, duration)*(_magnitude + magnitudeModifier);
        return (int)Math.abs(Math.round(finalDamage));
    }
    
    public final GameComponent getSender() {
        return sender;
    }
    
    public final GameComponent getReceiver() {
        return receiver;
    }
    
    @Override
    public final Object lookup(String key) {
	switch(key) {
	case "target":
	    return receiver;
	case "sender":
	    return sender;
	case "chance":
	    return getSuccessChance();
	case "magnitude":
	    return getMagnitude();
	case "range":
	    return sender.getPosition().range(receiver.getPosition());
	case "type":
	    return getClass().getSimpleName();
	case "cause":
	    return _cause;
	case "virtual":
	    return virtual ? this : null;
	default:
	    return super.lookup(key);
	}
    }
    
    @Override
    public final Tile getPosition() {
	return sender.getPosition();
    }
       
    @Override
    public final double getAccuracyFalloff() {
        return sender.getAccuracyFalloff();
    }
        
    protected final Tile getSenderPosition() {
	return sender.getPosition();
    }
    
    protected final Tile getReceiverPosition() {
	return receiver.getPosition();
    }
    
    /**
     * This is a stupid stopgap resulting from stupid visibility choices. Invokes
     * {@link #onJoinGame onJoinGame} on a Weapon. Used in weapon events.
     * @param w 
     * @deprecated it's a stopgap
     */
    @Deprecated
    protected final void magicAddWeapon(Weapon w) {
	w.onJoinGame();
    }
    
    @Override
    public final GameState getGameState() {
	return sender.getGameState();
    }
    
    @Override
    public final Player getPlayer() {
	return sender.getPlayer();
    }
    
    public final int getPriority() {
	return _priority;
    }
    
    public void cancel() {
	wasCancelled = true;
	successChance = 0;
	successModifier = 0;
    }
    
    protected boolean wasPosted() {
	return posted;
    }

    @Override
    public float toFloat(GameState gameState) {
	return 0;
    }
    
}
