package com.patchworkgalaxy.display.models;

import com.patchworkgalaxy.general.data.GameProps;
import java.util.List;

public class Stage {
    
    final float start, end;
    final AnimationDef animation;
    final boolean special;

    private Stage(AnimationDef animation, float start, float end, boolean special) {
        this.animation = animation;
        this.start = start;
	this.end = end;
        this.special = special;
    }
    
    public static Stage create(GameProps props) {
	
	float start = props.getFloat("Start");
	float end = props.getFloat("End");
	if(start > end)
	    throw new IllegalArgumentException("Attempted to create stage with negative duration");
	float duration = end - start;
	
	AnimationDef timer = new AnimationDef(new AnimationTimer("", Animation.Mode.STANDARD, duration));
	@SuppressWarnings("unchecked")
	List<AnimationDef> anims = props.get(List.class, "Anims");
	
	AnimationDef animation = new AnimationDef(new AnimationSupport("", Animation.Mode.STANDARD, timer, anims.toArray(new AnimationDef[anims.size()])));
	
	return new Stage(animation, start, end, props.getBoolean("Special!"));
	
    }
	
}