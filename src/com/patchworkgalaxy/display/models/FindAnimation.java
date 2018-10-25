package com.patchworkgalaxy.display.models;

import com.patchworkgalaxy.template.TemplateRegistry;

public class FindAnimation {
    
    private FindAnimation() {}
    
    static final AnimationDef MOVE = new AnimationDef(new AnimationMove("Move", Animation.Mode.STANDARD));
    static final AnimationDef TURN = new AnimationDef(new AnimationTurn("Turn", Animation.Mode.PRIORITY, 1));
    static final AnimationDef DEATH = new AnimationDef(new AnimationDummy("Death", Animation.Mode.PRIORITY));
    
    public static AnimationDef find(String name) {
	switch (name) {
	case "Move":
	case "Thruster":
	    return MOVE;
	case "Turn":
	case "Maneuver":
	    return TURN;
	case "Death":
	    return DEATH;
	default:
	    return TemplateRegistry.ANIMATIONS.lookup(name);
	}
    }
    
}
