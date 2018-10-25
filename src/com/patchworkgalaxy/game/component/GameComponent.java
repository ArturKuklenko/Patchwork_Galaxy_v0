package com.patchworkgalaxy.game.component;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.models.Model;
import com.patchworkgalaxy.display.models.Positional;
import com.patchworkgalaxy.game.misc.Faction;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Rangefinder;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.general.lang.Localizable;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.general.subscriptions.BaseSubscribable;
import com.patchworkgalaxy.template.Template;
import com.patchworkgalaxy.template.types.ConditionTemplate;

public abstract class GameComponent extends BaseSubscribable<GameEvent> implements Namespace, Positional, Localizable {
    
    protected final GameProps props;
    private final String _name, _displayName;
    private boolean _dead, _appearsDead, _autoupdate;
    private final int _flags;
    private final double _kineticAccuracyFalloff;

    private Model _model;
    private final short _templateId;
    private final Breed _breed;
    
    private final Subcomponents<Condition> _conditions;
    
    @SuppressWarnings("unchecked")
    protected GameComponent(GameProps props, Breed breed) {
	this.props = props.immutable();
	_name = props.getString("Name");
	_flags = props.getInt("Flags");
	_conditions = new Subcomponents<>();
	_autoupdate = true;
	_templateId = (short)props.getInt("TemplateId");
	_breed = breed;
	_displayName = getLocalizedString("name", _name);
        _kineticAccuracyFalloff = props.getFloat("Falloff");

    }
    
    public String getName() {
	return _name;
    }
    
    public String getDisplayName() {
	return _displayName;
    }
    
    public GameProps getProps() {
	return props;
    }
    
    public void turnStart() {
	for(Condition condition : _conditions)
	    condition.turnStart();
    }
    
    public void turnEnd() {
	for(Condition condition : _conditions)
	    condition.turnEnd();
    }
     
    public abstract double getAccuracyFalloff();
    
    public abstract Tile getPosition();

    
    public abstract Player getPlayer();
    
    public void addCondition(String conditionPath) {
	Object o = getGameState().lookup(conditionPath);
	if(!(o instanceof ConditionTemplate))
	    throw new IllegalArgumentException();
	Condition condition = ((ConditionTemplate)o).instantiate(this);
	_conditions.add(condition);
	condition.onJoinGame();
    }
    
    public void removeCondition(String conditionPath) {
	Object o = getGameState().lookup(conditionPath);
	if(!(o instanceof ConditionTemplate))
	    throw new IllegalArgumentException();
	@SuppressWarnings("unchecked")
	String cname = ((Template<Condition>)o).getName();
	_conditions.removeEldest(cname);
    }
    
    public boolean checkEvent(GameEvent gameEvent, boolean outgoing) {
	if(!gameEvent.isVirtual())
	    update(gameEvent);
	for(Condition condition : _conditions)
	    condition.checkEvent(gameEvent, outgoing);
	return true;
    }
    
    final boolean checkIncomingEvent(GameEvent gameEvent) {
	return checkEvent(gameEvent, false);
    }
    
    final boolean checkOutgoingEvent(GameEvent gameEvent) {
	return checkEvent(gameEvent, true);
    }
    
    public GameState getGameState() {
	return getPlayer().getGameState();
    }
    
    public boolean graphicValid() {
	return !_appearsDead;
    }
    
    public void dontAutoUpdate() {
	_autoupdate = false;
    }
    
    public void updateAppearance() {
	_appearsDead = _dead;
	_autoupdate = true;
    }
    
    public boolean alive() {
        return !_dead;
    }

    public void kill() {
	if(!_dead) {
	    _dead = true;
	    if(_autoupdate)
		updateAppearance();
	    update();
	}
    }
    
    public void onJoinGame() {
	if(this instanceof RenderableComponent)
	    _model = GameAppState.getInstance().getModel(this);
	getGameState().onComponentAdded(this);
    }
    
    Vector3f getDefaultHeading() {
	return new Vector3f(1, 0, 0);
    }
    
    public String getModelName() {
	return props.getString("Model");
    }
    
    public Model getModel() {
	return _model;
    }
    
    @Override
    public Vector3f getPositionVector() {
	Tile tile = getPosition();
	return tile != null ? tile.getOffsetPosition() : Vector3f.NAN;
    }
    
    protected void showGraphic() {
	Model m = getModel();
	m.setPosition(getPositionVector());
	m.setTarget(new Positional.FromVector(getPositionVector().add(getDefaultHeading())));
	m.setVisible(true);
	if(!(this instanceof PatchingShip))
	    m.play("Arrival");
    }
    
    @Override
    public Object lookup(String name) {
	switch (name) {
	case "player":
	    return getPlayer();
	case "alive":
	    return new Integer(alive() ? 1 : 0);
	case "position":
	    return getPosition();
	case "range":
	    return new Rangefinder(getPosition());
	case "conditions":
	    return _conditions;
	default:
	    return getGameState().lookup(name);
	}
    }
    
    public int getFlags() {
	int flags = _flags;
	for(Condition condition : _conditions)
	    flags |= condition.getFlags();	
	return flags;
    }
    
    public boolean checkFlags(int flags) {
	return (getFlags() & flags) != 0;
    }
    
    public short getTemplateId() {
	return _templateId;
    }
    
    public Faction getFaction() {
	return getPlayer().getFaction();
    }
    
    public Breed getBreed() {
	return _breed;
    }
    
    @Override public String[] getLocalizationNamespaces() {
	String breed = _breed.toString().toLowerCase();
	String name = breed + "." + getName().toLowerCase();
	try {
	    String faction = breed + "." + getFaction().getName().toLowerCase();
	    return new String[] {name, faction, breed};
	} catch(NullPointerException e) {
	    return new String[] {name, breed};
	}
    }
    
    public final String getLocalizedString(String key) {
	return getLocalizedString(key, "");
    }
    
    public final String getLocalizedString(String key, String defaultVal) {
	String result =  Localizer.getLocalizedString(this, key);
	if(result == null || result.isEmpty())
	    return defaultVal;
	return result;
    }
    
    /**
     * Checks if this component is owned by the local player, as determined by
     * {@link #getPlayer()} and {@link Player#getLocalPlayer()}.
     */
    public final Alignment getAlignment() {
	Player player = getPlayer();
	Player local = GameAppState.getLocalPlayer();
	if(player == null || local == null) return Alignment.NEUTRAL;
	return player.equals(local) ? Alignment.FRIENDLY : Alignment.HOSTILE;
    }
    
    public static enum Alignment {
	FRIENDLY, HOSTILE, NEUTRAL;
    }
    
}