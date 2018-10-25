package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Collections;
import java.util.Set;

public class TargetValidatorIsSelf extends TargetValidator {
    
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	return Collections.singleton(source.getPosition());
    }

    @Override public GameComponent autotarget(ShipSystem system) {
	return system.getShip();
    }
    
}
