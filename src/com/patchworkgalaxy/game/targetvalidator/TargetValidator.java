package com.patchworkgalaxy.game.targetvalidator;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.tile.Tile;
import java.util.Set;

public abstract class TargetValidator {
    
    /**
     * @see TargetValidatorIsEnemy
     */
    public static final TargetValidator IS_ENEMY = new TargetValidatorIsEnemy();
    /**
     * @see TargetValidatorIsAlly
     */
    public static final TargetValidator IS_ALLY = new TargetValidatorIsAlly();
    /**
     * @see TargetValidatorIsAdjacentEnemy
     */
    public static final TargetValidator IS_ADJACENT_ENEMY = new TargetValidatorIsAdjacentEnemy();
    /**
     * @see TargetValidatorIsAdjacentAlly
     */
    public static final TargetValidator IS_ADJACENT_ALLY = new TargetValidatorIsAdjacentAlly();
    /**
     * @see TargetValidatorIsEnemyHQ
     */
    public static final TargetValidator IS_ENEMY_HQ = new TargetValidatorIsEnemyHQ();
    /**
     * @see TargetValidatorIsFriendlyHQ
     */
    public static final TargetValidator IS_FRIENDLY_HQ = new TargetValidatorIsFriendlyHQ();
    /**
     * @see TargetValidatorIsFarEnemy
     */
    public static final TargetValidator IS_FAR_ENEMY = new TargetValidatorIsFarEnemy();   
    /**
     * @see TargetValidatorIsEmptyTile
     */
    public static final TargetValidator IS_EMPTY_TILE = new TargetValidatorIsEmptyTile();
    /**
     * @see TargetValidatorIsAdjacentEmptyTile
     */
    public static final TargetValidator IS_ADJACENT_EMPTY_TILE = new TargetValidatorIsAdjacentEmptyTile();
    /**
     * @see TargetValidatorTypeAMovement
     */
    public static final TargetValidator TYPE_A_MOVEMENT = new TargetValidatorTypeAMovement();
    /**
     * @see TargetValidatorFake
     */
    public static final TargetValidator FAKE = new TargetValidatorFake();
    /**
     * @see TargetValidatorIsSelf
     */
    public static final TargetValidator IS_SELF = new TargetValidatorIsSelf();
    
    /**
     * This method retrieves a target validator from a symbolic name.
     * @param name 
     * @return a validator matching that symbolic name
     * @throws IllegalArgumentException if there is no validator with that name.
     */
    public static TargetValidator getByName(String name) {
	switch (name) {
	case "IsEnemy":
	    return IS_ENEMY;
	case "IsAlly":
	    return IS_ALLY;
	case "IsAdjacentEnemy":
	    return IS_ADJACENT_ENEMY;
	case "IsAdjacentAlly":
	    return IS_ADJACENT_ALLY;
	case "IsEnemyHQ":
	    return IS_ENEMY_HQ;
	case "IsFriendlyHQ":
	    return IS_FRIENDLY_HQ;
	case "IsFarEnemy":
	    return IS_FAR_ENEMY;
	case "IsEmptyTile":
	    return IS_EMPTY_TILE;
	case "IsAdjacentEmptyTile":
	    return IS_ADJACENT_EMPTY_TILE;
	case "TypeAMovement":
	    return TYPE_A_MOVEMENT;
	case "IsSelf":
	    return IS_SELF;
	case "Fake":
	    return FAKE;
	default:
	    throw new IllegalArgumentException("Could not find target validator with name " + name);
	}
    }
    
    public boolean validateShot(GameComponent source, Tile target) {
	Set<Tile> valid = getValidTargets(source);
	return valid != null && valid.contains(target);
    }
        
    public GameComponent autotarget(ShipSystem shipSystem) {
        return null;
    }
    
    public abstract Set<Tile> getValidTargets(GameComponent source);
    
    public boolean usesSpecialTargeting(ShipSystem shipSystem) {
	return false;
    }
    
    public String getTargetingString(ShipSystem shipSystem) {
	return "Select target for " + shipSystem.getDisplayName();
    }
    
    public boolean isFake() {
	return false;
    }
    
}
