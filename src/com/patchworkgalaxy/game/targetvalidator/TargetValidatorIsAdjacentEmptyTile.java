package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Iterator;
import java.util.Set;

/**
 * A TargetValidator that ensures a tile is adjacent and empty.
 * @author redacted
 */
public class TargetValidatorIsAdjacentEmptyTile extends TargetValidator {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Set<Tile> adjacent = source.getPosition().getAdjacency();
	Iterator<Tile> i = adjacent.iterator();
	while(i.hasNext()) {
	    if(i.next().getShip() != null)
		i.remove();
	}
	return adjacent;
    }
    
}