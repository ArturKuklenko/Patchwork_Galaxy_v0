package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.HashSet;
import java.util.Set;

public class TargetValidatorIsEnemyHQ extends TargetValidatorIsEnemy {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	Player p = source.getPlayer();
	Set<Tile> result = new HashSet<>();
	for(Player player : source.getGameState().getPlayers()) {
	    if(player == p) continue;
	    GameComponent hq = player.getHeadquarters();
	    Tile tile = hq == null ? null : hq.getPosition();
	    if(tile != null)
		result.add(tile);
	}
	return result;
    }
    
}
