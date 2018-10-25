package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A TargetValidator that ensures a tile contains an enemy ship.
 * @author redacted
 */
public class TargetValidatorIsEnemy extends TargetValidator {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	List<Player> players = source.getGameState().getPlayers();
	List<Ship> ships = new ArrayList<>();
	Player p = source.getPlayer();
	for(Player player : players) {
	    if(player != p)
		ships.addAll(player.getShips());
	}
	Set<Tile> result = new HashSet<>();
	for(Ship ship : ships) {
	    Tile tile = ship.getPosition();
	    if(tile != null)
		result.add(tile);
	}
	return result;
    }
    
}
