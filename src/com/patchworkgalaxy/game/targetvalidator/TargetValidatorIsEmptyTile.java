package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Iterator;
import java.util.Set;

/**
 * A TargetValidator that ensures a tile is empty.
 * @author redacted
 */
public class TargetValidatorIsEmptyTile extends TargetValidator {

    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Set<Tile> result = source.getGameState().getBoard().getAllTiles();
	Iterator<Tile> i = result.iterator();
	while(i.hasNext()) {
	    Tile tile = i.next();
	    if(tile == null || tile.getShip() != null)
		i.remove();
	}
	return result;
    }
    
}
