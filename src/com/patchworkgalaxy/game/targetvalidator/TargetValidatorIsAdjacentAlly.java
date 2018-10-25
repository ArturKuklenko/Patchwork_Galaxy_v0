package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Iterator;
import java.util.Set;

/**
 * A TargetValidator that ensures a tile is adjacent and contains an allied ship.
 * @author redacted
 */
public class TargetValidatorIsAdjacentAlly extends TargetValidator {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Player player = source.getPlayer();
	Set<Tile> adjacent = source.getPosition().getAdjacency();
	Iterator<Tile> i = adjacent.iterator();
	while(i.hasNext()) {
	    Ship ship = i.next().getShip();
	    if(ship == null || ship.getPlayer() != player)
		i.remove();
	}
	return adjacent;
    }
    
}
