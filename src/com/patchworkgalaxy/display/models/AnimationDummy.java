package com.patchworkgalaxy.display.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.patchworkgalaxy.game.component.Ship;

class AnimationDummy extends Animation {

    private boolean _done;
    
    AnimationDummy(String trigger, Mode mode) {
	super(trigger, mode, null);
    }
    
    @Override
    protected void initialize() {}

    @Override
    protected void teardown() {
    }

    @Override
    protected boolean isDoneImpl() {
	return _done;
    }

    @Override
    protected void animate(float tpf) {
	_done = true;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
