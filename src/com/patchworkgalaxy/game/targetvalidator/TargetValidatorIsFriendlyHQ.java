package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TargetValidatorIsFriendlyHQ extends TargetValidator {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	GameComponent hq = source.getPlayer().getHeadquarters();
	Tile tile = hq == null ? null : hq.getPosition();
	return tile == null ? new HashSet<Tile>() : Collections.singleton(tile);
    }
    
}
