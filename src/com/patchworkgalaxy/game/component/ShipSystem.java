package com.patchworkgalaxy.game.component;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.display.models.AnimationDef;
import com.patchworkgalaxy.display.models.Model;
import com.patchworkgalaxy.display.models.Positional;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.game.state.evolution.EvolutionBuilder;
import com.patchworkgalaxy.game.targetvalidator.TargetValidator;
import com.patchworkgalaxy.game.targetvalidator.TargetValidatorTypeAMovement;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.Resolver;
import com.patchworkgalaxy.template.parser.CanBeEvent;
import com.patchworkgalaxy.template.types.ShipSystemTemplate;
import java.util.Set;

public class ShipSystem extends GameComponent {
    
    private final Ship _ship;
    private final Resolver _launchEvent;
    private final TargetValidator _targetValidator;
    private GameComponent _target;
    private final int _thermalCost;
    private boolean _recentlyFired;
    private final int _reusable;
    private final String _shortName;
    private final String _hotkey;
    private final boolean _specialOrder, _fakeOrder;
    
    private final boolean _isEngine, _isWeapon;
    
    private final AnimationDef _animation;
    private final boolean _vls;
    
    private final ShipSystemTemplate _template;
    
    public ShipSystem(ShipSystemTemplate template, Ship ship) {
	super(template.getProps(), Breed.SYSTEM);
	_template = template;
	_shortName = props.getString("ShortName");
	_ship = ship;
	String launchName = props.getString("LaunchEvent");
	_launchEvent = new Resolver(getGameState(), props.getString("LaunchEvent"), true);
	_targetValidator = TargetValidator.getByName(props.getString("TargetValidator"));
	_thermalCost = props.getInt("ThermalCost");
	_reusable = props.getInt("Reusable");
	_hotkey = props.getString("Hotkey");
	_animation = props.get(AnimationDef.class, "Animation");
	_vls = props.getBoolean("VLS!");
	_specialOrder = props.getBoolean("Special!");
	_fakeOrder = _targetValidator.isFake();
	_isEngine = _shortName.contains("Engine");
	_isWeapon = launchName.contains("weapon:");
    }
     
    public boolean autotarget() {
	GameComponent target = _targetValidator.autotarget(this);
	if(target != null) {
	    aim(target.getPosition());
	    return true;
	}
	else
	    return false;
    }
    
    public boolean setTarget(GameComponent target) {
	Tile tile = target.getPosition();
	if(!canFire())
            return false;
	
        if(_targetValidator.validateShot(_ship, tile)) {
            int cost = getThermalCost();
            if(_ship.getThermalLimit(isWeapon(), isEngine()) >= cost) {
                _target = target;
		return true;
            }
        }
	
	return false;
    }
    
    public boolean aim(Tile target) {
	if(canFire()&& canTarget(target)) {
	    getGameState().safeEvolve(new EvolutionBuilder(this).shift(_ship).shift(target).getEvolution());
	    return true;
	}
	else
	    return false;
    }
    
    /**
     * Directs the system to play any firing animations and fire.
     * Do not call this except in response to a relevant network message!
     * @param target the tile to fire at
     */
    public void fire(GameComponent target) {
	if(setTarget(target)) {
	    playFireAnimation();
	    fire0();
	}
	else
	    throw new IllegalStateException("System " + getName() + " aimed from illegal state");
    }
    
    private void playFireAnimation() {
	getModel().setTarget(new Positional.FromVector(_target.getPosition().getOffsetPosition()));
	if(!_vls)
	    getModel().play("Maneuver");
	if(_animation != null)
	    getModel().play(_animation);
    }
    
    private void fire0() {
	if(_target == null)
	    throw new IllegalStateException("System fired with no target");
	_launchEvent.resolve(CanBeEvent.class).toEvent(_ship, _target).enqueue();
        _ship.thermalDamage(getThermalCost(), isWeapon(), isEngine());
        _recentlyFired = true;
	_target = null;
    }
    
    public ColoredText getTargetingTooltip(Tile target) {
	
	if(isEngine()) {
	    return new ColoredText("Cost: " + getPosition().range(target) + " TB");
	}
	
	ColoredText text = new ColoredText();
	GameEvent probe = _launchEvent.resolve(CanBeEvent.class).toEvent(_ship, target).virtualize();
	int magnitude = probe.getfinalDamage();
                        System.out.println("DisplayDamage = " + magnitude);

	float successChance = (float)probe.getFalloffPerTile();
	int displayChance = Math.round(successChance * 100);
	ColorRGBA hitColor = new ColorRGBA(1 - successChance, successChance, 0, 1);
	
	text.addText("Falloff Per Tile: " );
	text.addText(displayChance + "%", hitColor);
	text.addText("\nDamage: " + magnitude);
	
	/*Ship ship = target.getShip();
	if(ship != null) {
	    int currentHull = ship.getHullIntegrity();
	    int maxHull = ship.getMaxHullIntegrity();
	    int currentShield = ship.getShieldIntegrity();
	    int maxShield = ship.getMaxShieldIntegrity();
	    float hullPercent = (float)currentHull / (float)maxHull;
	    float shieldPercent = (float)currentShield / (float)maxShield;
	    if(currentShield == maxShield)
		shieldPercent = 1;
	    ColorRGBA hullColor = new ColorRGBA(1 - hullPercent, .8f * hullPercent, 0, 1);
	    ColorRGBA shieldColor = new ColorRGBA(1 - shieldPercent, shieldPercent, shieldPercent, 1);
	    text.addText("\nTarget: ");
	    text.addText(currentHull + "/" + maxHull, hullColor);
	    if(maxShield > 0)
		text.addText(" (" + currentShield + "/" + maxShield + ")", shieldColor);
	}*/
	
	return text;
    }
    
    public String getShortName() {
	return _shortName;
    }
    
    public boolean canFire() {
	return !(_ship instanceof PatchingShip)
		&& ((_specialOrder && !getPlayer().hasArrivingShips()) || getPlayer().canIssueOrders())
		&& !_fakeOrder
		&& isAwake()
		&& shipCanAffordFiring()
		&& _ship.alive();
    }
    
    public boolean canFireLocally() {
	return canFire() && Player.isLocal(getPlayer());
    }
    
    public boolean isAwake() {
        return _target == null && (!_recentlyFired || isReusable());
    }
    
    public ShipSystem getAwakeSibling() {
	return _ship.getSystem(getName(), true);
    }
    
    public boolean shipCanAffordFiring() {
	if(isEngine() && _ship.getThermalLimit(false, true) <= 0)
	    return false;
	return _ship.getThermalLimit(isWeapon(), false) >= _thermalCost;
    }
    
    public boolean canTarget(Tile tile) {
	if(tile == null)
	    return false;
	return _targetValidator.validateShot(_ship, tile);
    }
    
    public Ship getShip() {
        return _ship;
    }
    
    @Override
    public boolean alive() {
        return _ship.alive();
    }
    
    @Override
    public Tile getPosition() {
        return _ship.getPositionOrCarrierPosition();
    }
    
    public int getThermalCost() {
	if(this._targetValidator == TargetValidator.TYPE_A_MOVEMENT)
	    return TargetValidatorTypeAMovement.getTypeAMoveCost();
        return _thermalCost;
    }
    
    @Override
    public Player getPlayer() {
        return _ship.getPlayer();
    }

    @Override
    public void turnStart() {
	super.turnStart();
        _recentlyFired = false;
    }
    
    public GameComponent retrieveRecentTarget() {
        GameComponent target = _target;
        _target = null;
        return target;
    }
    
    public boolean isTypeAEngine() {
	return _targetValidator == TargetValidator.TYPE_A_MOVEMENT;
    }
    
    public boolean isReusable() {
	if(_reusable == 0)
	    return false;
	if(_reusable > 0)
	    return true;
	return _ship.isTypeA();
    }

    public boolean isWeapon() {
	return _isWeapon;
    }
    
    public boolean isEngine() {
	return _isEngine;
    }
    
    public char getHotkey() {
	if(_hotkey == null || _hotkey.length() == 0)
	    return 0;
	return _hotkey.charAt(0);
    }
    
    public String getTargetingString() {
	return "Select target for " + getName();
    }
    
    public boolean isSpecialOrder() {
	return _specialOrder;
    }
    
    public boolean isFakeOrder() {
	return _fakeOrder;
    }
    
    public TargetValidator getTargetValidator() {
	return _targetValidator;
    }
    
    @Override
    public Model getModel() {
	return _ship.getModel();
    }
    
    @Override
    public boolean checkEvent(GameEvent gameEvent, boolean outgoing) {
	_ship.checkEvent(gameEvent, outgoing);
	return super.checkEvent(gameEvent, outgoing);
    }
    
    public ShipSystemTemplate getTemplate() {
	return _template;
    }
    
    public Set<Tile> getLegalTargets() {
	return _targetValidator.getValidTargets(_ship);
    }

    @Override
    public double getAccuracyFalloff() {
return _ship.getAccuracyFalloff();
    
    }

}