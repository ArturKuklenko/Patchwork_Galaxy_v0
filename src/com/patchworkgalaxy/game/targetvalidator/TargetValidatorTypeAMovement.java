package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Iterator;
import java.util.Set;

public class TargetValidatorTypeAMovement extends TargetValidator {

    static int cost = 0;

    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Ship moving = source instanceof Ship ? (Ship)source : null;
	Set<Tile> result = source.getGameState().getBoard().getAllTiles();
	Iterator<Tile> i = result.iterator();
	while(i.hasNext()) {
	    Tile tile = i.next();
	    if(tile == null) {
		i.remove();
		continue;
	    }
	    Ship collision = tile.getShip();
	    if(collision != null && !collision.canShipEnterHangar(moving))
		i.remove();
	}
	return result;
    }
    
    @Override public boolean validateShot(GameComponent source, Tile target) {
	Ship ship = source instanceof Ship ? (Ship)source : null;
	if(ship == null)
	    return false;
	cost = ship.getPositionOrCarrierPosition().pathingCostForShip(ship, target, true);
	int speed = ship.getSpeed();
        return cost <= speed && super.validateShot(ship, target);
    }
    
    /**
     * 
     * @return The cost of this movement.
     */
    public static int getTypeAMoveCost() {
        return cost;
    }
    
}
