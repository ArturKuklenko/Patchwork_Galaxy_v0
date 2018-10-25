package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.game.component.Ship;

class AnimationPatchIn extends AnimationMove {

    AnimationPatchIn(String trigger, Mode mode) {
	super(trigger, mode);
    }
    
    @Override protected void initialize() {
	_target = new Vector3f(spatial.getLocalTranslation());
	Vector3f offset = new Vector3f(model.getTarget()).subtractLocal(_target);
	offset.normalizeLocal().multLocal(-300);
	_initial = _target.add(offset);
	_duration = .1f;
	_scratch = new Vector3f();
    }
    
    @Override protected void animate(float tpf) {
	super.animate(tpf);
    }
    
}
