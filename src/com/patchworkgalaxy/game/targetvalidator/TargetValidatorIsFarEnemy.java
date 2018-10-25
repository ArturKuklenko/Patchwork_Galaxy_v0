package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.HashSet;
import java.util.Set;

/**
 * A TargetValidator that ensures a tile contains an enemy and is not adjacent.
 * @author redacted
 */
public class TargetValidatorIsFarEnemy extends TargetValidatorIsEnemy {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Player p = source.getPlayer();
	Set<Tile> result = new HashSet<>();
	for(Player player : source.getGameState().getPlayers()) {
	    if(player == p) continue;
	    for(Ship ship : player.getShips()) {
		Tile tile = ship.getPosition();
		if(tile != null)
		    result.add(tile);
	    }
	}
	result.removeAll(source.getPosition().getAdjacency());
	return result;
    }
    
}
