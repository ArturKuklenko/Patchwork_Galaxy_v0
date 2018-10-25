package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Set;

public class TargetValidatorFake extends TargetValidator {
    
    @Override public Set<Tile> getValidTargets(GameComponent source) {
	return null;
    }
    
    @Override public boolean isFake() {
	return true;
    }
    
}
