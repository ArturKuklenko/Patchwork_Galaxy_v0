package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;

class CommandCardController {
    
    private Ship _ship;
    private ShipSystem _system;
    private boolean _visible;
    private final Vector2f _center;
    private final GameUIPD _gameUI;
    
    CommandCardController(GameUIPD gameUI) {
	_gameUI = gameUI;
	_center = new Vector2f();
    }
    
    void update(Ship ship, ShipSystem system) {
	if(system != null && ship == null)
	    ship = system.getShip();
	_system = system;
	if(!isVisible(ship) || system != null) {
	    hide();
	    return;
	}
	Vector2f center = PatchworkGalaxy.getScreenCoordinates(ship.getModel().getSpatial());
	if(ship.equals(_ship) && _center.equals(center) && _visible)
	    return;
	_center.set(center);
	_ship = ship;
	CCPanelDescriptor ccp = new CCPanelDescriptor(_gameUI, _center, _ship);
	UI.Instance.showPanelWithTag(ccp, "Command Card");
	_visible = true;
    }
    
    void update() {
	update(_ship, _system);
    }
    
    boolean isVisible() {
	return isVisible(_ship);
    }
    
    Ship getShip() {
	return _ship;
    }
    
    ShipSystem getSystem() {
	return _system;
    }
    
    private boolean isVisible(Ship ship) { 
	return	ship != null &&
		ship.getModel().isVisible() &&
		!ship.getModel().isMoving() &&
		ship.getPosition() != null &&
		!PatchSpacePanelDescriptor.isActive();
    }
    
    private void hide() {
	UI.Instance.showPanelWithTag(null, "Command Card");
	_visible = false;
    }
    
}