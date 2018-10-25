package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.HashSet;
import java.util.Set;

/**
 * A TargetValidator that ensures a tile contains an allied ship.
 * @author redacted
 */
public class TargetValidatorIsAlly extends TargetValidator {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Set<Ship> ships = source.getPlayer().getShips();
	Set<Tile> tiles = new HashSet<>();
	for(Ship ship : ships) {
	    Tile tile = ship.getPosition();
	    if(tile != null)
		tiles.add(tile);
	}
	return tiles;
    }
    
}
