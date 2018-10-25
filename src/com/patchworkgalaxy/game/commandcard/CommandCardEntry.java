package com.patchworkgalaxy.game.commandcard;

import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.template.types.ShipSystemTemplate;

public class CommandCardEntry {
    
    private final ShipSystemTemplate _systemTemplate;
    private final Ship _ship;
    private final int _max, _cost;
    private final String _name, _displayName, _description, _hotkey, _icon;
    private final boolean _reusable, _fake, _weapon, _engine;
    
    CommandCardEntry(ShipSystem system) {
	_systemTemplate = system.getTemplate();
	_ship = system.getShip();
	_weapon = system.isWeapon();
	_engine = system.isEngine();
	_name = _systemTemplate.getName();
	_displayName = _systemTemplate.getDisplayName();
	_cost = _systemTemplate.getInt("ThermalCost");
	_description = _systemTemplate.getDescription();
	_hotkey = _systemTemplate.getString("Hotkey");
	_icon = "Interface/" + _systemTemplate.getString("Icon");
	_fake = system.isFakeOrder();
	_reusable = system.isReusable();
	_max = _ship.countSystems(_name, false);
    }
    
    public String getDisplayName() {
	return _displayName;
    }
    
    public int getAvailable() {
	return _ship.countAwakeSystems(_name);
    }
    
    public int getMax() {
	return _max;
    }
    
    public int getCost() {
	return _cost;
    }
    
    public String getHotkey() {
	return _hotkey;
    }
    
    public String getDescription() {
	return _description;
    }
    
    public String getIcon() {
	return _icon;
    }
    
    public boolean canFire() {
	if(!_ship.canReceiveOrders()) return false;
	if(_ship.getPlayer() != GameAppState.getLocalPlayer()) return false;
	if(isFake()) return false;
	if(!isAffordable()) return false;
	if(_reusable) return true;
	return getAvailable() > 0;
    }
    
    public ShipSystem getSystem() {
	return _ship.getSystem(_name, true);
    }
    
    public boolean isAffordable() {
	int cost = _engine ? 1 : getCost();
	return cost <= _ship.getThermalLimit(_weapon, _engine);
    }
    
    public boolean isFake() {
	return _fake;
    }
    
}
