package com.patchworkgalaxy.display.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

class AnimationTimer extends Animation {

    private float _duration;
    
    AnimationTimer(String trigger, Mode mode, float duration) {
	super(trigger, mode, null);
	_duration = duration;
    }
    
    @Override
    protected void initialize() {}

    @Override
    protected void teardown() {}

    @Override
    protected boolean isDoneImpl() {
	if(spatial == null)
	    return false;
	return _duration <= 0 || spatial.getParent() == null;
    }

    @Override
    protected void animate(float tpf) {
	_duration -= tpf;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
