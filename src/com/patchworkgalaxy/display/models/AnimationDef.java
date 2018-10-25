package com.patchworkgalaxy.display.models;

import com.patchworkgalaxy.display.models.Animation.Mode;
import com.patchworkgalaxy.game.component.Weapon;
import com.patchworkgalaxy.template.types.SpewerTemplate;

public class AnimationDef {
    
    private final Animation _prototype;
    
    AnimationDef(Animation prototype) {
	if(prototype == null)
	    _prototype = null;
	else
	    _prototype = prototype.clone();
    }
    
    public static AnimationDef forSpewer(SpewerTemplate spewer, String pointname, float scale) {
	AnimationSpewer animation = new AnimationSpewer("", Animation.Mode.STANDARD, spewer);
	animation.pointname = pointname;
	animation.scale = scale;
	return new AnimationDef(animation);
    }
    
    public static AnimationDef forWeapon(Weapon weapon) {
	Animation animation = new AnimationWeapon(weapon, Mode.STANDARD);
	animation.pointname = weapon.getProps().getString("ShortName");
	return new AnimationDef(animation);
    }
    
    Animation getAnimation(Model model) {
	return _prototype.withModel(model);
    }
    
    String getTrigger() {
	return _prototype.getTrigger();
    }
    
}
