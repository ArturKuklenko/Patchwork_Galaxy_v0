package com.patchworkgalaxy.game.component;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.game.event.CaptureEvent;
import com.patchworkgalaxy.game.misc.Tech;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileGroup;
import com.patchworkgalaxy.game.vital.Vital;
import com.patchworkgalaxy.game.vital.VitalStore;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipTemplate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Ship extends GameComponent implements Selectable, RenderableComponent, Observer {
    
    private Short _id;
    
    private Tile _position;
    
    private final VitalStore _vitals;
    private final Vital _hull, _shield, _vitality,
	    _thermal, _weaponThermal, _moveThermal;
    
    private final Subcomponents<ShipSystem> _systems;
    
    private final Player _player;
	
    private final double _kineticAccuracyFalloff;
    private final int _tier;
    private final int _shieldRegen;
    private boolean _hasTypeAEngine;
    private final boolean _heroic;
    private final String _designation;
    private final HangarDeck _hangarDeck;
    private Ship _carriedBy,_randomShip;
    private boolean _hangarGraphic;

    public Ship(GameProps props, Player owner) {
	super(props, Breed.SHIP);
	_player = owner;
	
	_vitals = new VitalStore();
	_hull = _vitals.addVital("hull", props.getInt("Hull"));
	_shield = _vitals.addVital("shield", props.getInt("Shield"));
	_vitality = _vitals.stackVitals("vitality", "hull", "shield");
	_thermal = _vitals.addVital("thermal", props.getInt("Thermal"));
	_weaponThermal = _vitals.addVital("weapon_thermal", 0);
	_moveThermal = _vitals.addVital("move_thermal", 0);
	_shieldRegen = props.getInt("ShieldRegen");
	_tier = props.getInt("Tier");
	_kineticAccuracyFalloff = props.getFloat("Falloff");
	_systems = new Subcomponents<>();
	String systems = props.getString("System");
	if(systems != null) {
	    for(String s : systems.split(","))
		initSystem(s.trim());
	}
	String hangars = props.getString("Hangar");
	if(hangars != null){
            _hangarDeck = new HangarDeck(this, hangars.split(","));
            _hangarDeck.getCurrentHangar();
           //_randomShip = _hangarDeck.getCurrentHangar().getRandomShip().instantiate(_player,_position );
        }
        else{
            _randomShip=null;
	    _hangarDeck = new HangarDeck(this, null);
        }
        _hasTypeAEngine = props.getBoolean("TypeA!");
	_heroic = props.getBoolean("Heroic!");
	if(this instanceof PatchingShip)
	    _designation = super.getDisplayName();
	else
	    _designation = Localizer.getLocalizedString(this, "designation", getGameState().getRandom());
    }
    
    public short getId() {
	return _id;
    }
    
    public void register(short id) {
	if(_id != null) throw new IllegalStateException("Tried to reregister a ship");
	_id = id;
    }
    
    private void initSystem(String system) {
	ShipSystem ss = TemplateRegistry.SYSTEMS.lookup(system).instantiate(this, _systems.size());
	_systems.add(ss);
    }
    
    @Override
    public Tile getPosition() {
        return _position;
    }
        
    @Override
    public double getAccuracyFalloff() {
        return _kineticAccuracyFalloff;
    }
    
    public Tile getPositionOrCarrierPosition() {
	if(_carriedBy != null)
	    return _carriedBy.getPositionOrCarrierPosition();
	return getPosition();
    }
    
    public boolean canPathThrough(Tile tile) {
	if(tile == null)
	    return false;
	if(evasiveMovement())
	    return true;
	Ship collide = tile.getShip();
	if(collide == null)
	    return true;
	return collide.getPlayer() == _player;
    }
    
    public boolean setPosition(Tile newPosition) {
	boolean result;
	Tile oldPosition = _position;
	Ship collision = newPosition == null ? null : newPosition.getShip();
	if(collision == null) {
	    _position = newPosition;
	    if(_position != null) {
		try {
		    _position.setShip(this);
		    result = true;
		} catch(IllegalStateException | IllegalArgumentException e) {
		    result = false;
		}
	    }
	    else
		result = true;
	    if(_carriedBy != null) {
		_carriedBy.launchShipFromHangar(this);
		PatchworkGalaxy.schedule(new Runnable() {
		    @Override public void run() {
			_carriedBy = null;
		    }
		});
	    }
	}
	else
	    result = collision.addShipToHangar(this);
	if(result && oldPosition != null)
	    oldPosition.setShip(null);
	return result;
    }
       
   
    @Override
    Vector3f getDefaultHeading() {
	return getPlayer().getDefaultHeading();
    }

    public int getHullIntegrity() {
        return _hull.getCurrent();
    }
    
    public int getShieldIntegrity() {
        return _shield.getCurrent();
    }
    
    public boolean hasSystemsAvailable() {
	boolean result = false;
	for(ShipSystem system : _systems) {
	    if(system.canFire()) {
		result = true;
		break;
	    }
	}
	return result;
    }
	
    public int getThermalLimit(boolean forWeapon, boolean forEngine) {
	if(forWeapon)
	    return _thermal.getCurrent() + _weaponThermal.getCurrent();
	if(forEngine)
	    return _thermal.getCurrent() + _moveThermal.getCurrent();
        return _thermal.getCurrent();
    }
    
    public int getSpeed() {
	int speed = getThermalLimit(false, true);
	return isTypeA() ? speed : Math.min(speed, 1);
    }
    
    public int getMaxHullIntegrity() {
	return _hull.getMax();
    }
    
    public int getMaxShieldIntegrity() {
	return _shield.getMax();
    }
    
    public int getMaxThermalLimit() {
	return _thermal.getMax();
    }
    
    public double getHullPercent() {
	return _hull.getPercent();
    }
    
    public double getShieldPercent() {
	return _shield.getPercent();
    }

    public int getMovesLeft() {
	int tl = getThermalLimit(false, true);
	if(this._hasTypeAEngine || checkFlags(1))
	    return tl;
	return (tl > 0) ? 1 : 0;
    }
    
    /**
     * @return True if this ship can move through enemy ships.
     */
    public boolean evasiveMovement() {
	return checkFlags(2);
    }
    
    @Override
    public void turnStart() {
	super.turnStart();
        _shield.regenerate(_shieldRegen);
        _thermal.regenerate();
	_weaponThermal.damage();
	_moveThermal.damage();
        //this.addShipToHangar(_randomShip);
	for(ShipSystem system : _systems)
	    system.turnStart();
	if(_position != null) {
	    TileGroup strategicGroup = _position.getStrategicGroup();
	    if(strategicGroup != null)
		CaptureEvent.getCaptureEvent(this, strategicGroup).enqueue();
	}
    }
    
    public int thermalDamage(int amount, boolean forWeapon, boolean forEngine) {
	if(forWeapon)
	    amount = _weaponThermal.damage(amount);
	if(forEngine)
	    amount = _moveThermal.damage(amount);
        return _thermal.damage(amount);
    }
    
    public double getMiracleVulnerability() {
        return .05;
    }
    
    @Override
    public void onJoinGame() {
	super.onJoinGame();
	for(Tech tech : getPlayer().techs)
	    tech.arrivingShipApplication(this);
	if(!isPatchingShip()) {
	    for(ShipSystem system : _systems)
		system.onJoinGame();
	    getPlayer().addShip(this);
	    _vitality.addObserver(this);
	}
	showGraphic();
    }
    
    @Override
    public void kill() {
        super.kill();
        getPlayer().removeShip(this);
	setPosition(null);
	if(isHeroic())
	    getPlayer().kill();
    }
    
    @Override
    public void updateAppearance() {
	super.updateAppearance();
	if(!alive()) {
	    if((this instanceof PatchingShip)){
		getModel().setVisible(false);
            }else{
		getModel().play("Death");
            }
	}
    }
    
    public boolean isOwnedLocally() {
        return Player.isLocal(getPlayer());
    }
    
    public boolean canReceiveOrders() {
        return getPlayer().isCurrentPlayer();
    }

    @Override public ColoredText getDescription() {
	//being removed
	return new ColoredText();
    }
    
    @Override
    public Object lookup(String name) {
	switch (name) {
	case "hull":
	    return _hull;
	case "shield":
	case "shields":
	    return _shield;
	case "shield_regen":
	    return _shieldRegen;
	case "vitality":
	    return _vitality;
	case "thermal":
	    return _thermal;
	case "weapon_thermal":
	    return _weaponThermal;
	case "move_thermal":
	    return _moveThermal;
	case "tier":
	    return _tier;
	case "falloff":
	    return _kineticAccuracyFalloff;
	case "miracle_vulnerability":
	    return getMiracleVulnerability();
	case "ship":
	    return this;
	case "near_allies":
	    //placeholder hardcoded crud incoming!
	    int count = 0;
	    for(Tile tile : getPosition().getAdjacency()) {
		Ship s = tile.getShip();
		if(s != null && s != this &&s.getPlayer() == this.getPlayer())
		    ++count;
	    }
	    return count;
	default:
	    return super.lookup(name);
	}
    }
    
    public boolean isHeroic() {
	return _heroic;
    }

    public boolean isTypeA() {
	return _hasTypeAEngine;
    }
    
    public int countAwakeSystems(String name) {
	return countSystems(name, true);
    }
    
    public int countSystems(String name, boolean requireAwake) {
	int result = 0;
	Iterable<ShipSystem> candidates = _systems.getNamedBucket(name);
	for(ShipSystem system : candidates) {
	    if(!requireAwake || system.isAwake())
		++result;
	}
	return result;
    }
    
    public List<ShipSystem> getUniqueSystems() {
	return _systems.getUnique();
    }
    
    public ShipSystem getSystem(String name, boolean requireAwake) {
	Iterable<ShipSystem> candidates = _systems.getNamedBucket(name);
	for(ShipSystem system : candidates) {
	    if(!requireAwake || system.isAwake())
		return system;
	}
	return null;
    }
    
    public boolean isPatchingShip() {
	return this instanceof PatchingShip;
    }
    
    public boolean isHeadquarters() {
	return this.equals(getPlayer().getHeadquarters());
    }

    @Override
    public Player getPlayer() {
	return _player;
    }
    
    public int getTier() {
	return _tier;
    }

    @Override
    public void update(Observable o, Object arg) {
	if(o.equals(_vitality)) {
	    if(_vitality.getCurrent() <= 0)
		kill();
	}
    }

    @Override
    public boolean checkEvent(GameEvent gameEvent, boolean outgoing) {
	Player p = getPlayer();
	if(p != null)
	    p.checkEvent(gameEvent, outgoing);
	Tile tile = getPosition();
	if(tile != null)
	    tile.checkEvent(gameEvent, outgoing);
	for(Condition condition : getGameState().getGlobalConditions())
	    condition.checkEvent(gameEvent, outgoing);
	return super.checkEvent(gameEvent, outgoing);
    }
    
    @Override public String getDisplayName() {
	return _designation;
    }
    //adolfnewStart
    public Integer getHangarCapacity() {
        if (_hangarDeck.getCurrentHangar() != null) {
            return _hangarDeck.getCurrentHangar().getHangarCapacity();   
        }else {
            return 0;
        }
    }
    //adolfnewEnd
    public boolean canShipEnterHangar(Ship ship) {
	return _hangarDeck.canAcceptShip(ship);
    }
    
    public boolean addShipToHangar(Ship ship) {
	if(_hangarDeck.acceptShip(ship)) {
	    ship._carriedBy = this;
	    return true;
	}
	return false;
    }
    
    public boolean launchShipFromHangar(Ship ship) {
	return _hangarDeck.launchShip(ship);
    }
    
    public void dispatchHangarEvent(GameComponent sender, String eventName, GameEvent cause) {
	_hangarDeck.dispatchHangarEvent(sender, eventName, cause);
    }
    
    public List<Ship> getDockedShips() {
	return _hangarDeck.getShips();
    }
    //adolfnew
    public ShipTemplate getCompatibleTemplate() {
        return this._hangarDeck.getCurrentHangar().getCompatibleTemplate();
    }
    
    
    public void setCarriedBy(Ship destship) {
        this._carriedBy = destship;
    }
    
    //adolfnew END
    
    public Ship getCarriedBy() {
	return _carriedBy;
    }
    
    public void setHangarGraphic(boolean updateHangarGraphic) {
	_hangarGraphic = updateHangarGraphic;
    }
    
    public boolean getHangarGraphic() {
	return _hangarGraphic;
    }
    
}
