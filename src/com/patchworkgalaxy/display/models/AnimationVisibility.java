package com.patchworkgalaxy.display.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial.CullHint;

class AnimationVisibility extends Animation {

    private final boolean _visible;
    
    AnimationVisibility(String trigger, boolean visible) {
	super(trigger, Mode.BACKGROUND, null);
	_visible = visible;
    }
    
    @Override
    protected void initialize() {
	if(_visible)
	    spatial.setCullHint(CullHint.Inherit);
	else
	    spatial.setCullHint(CullHint.Always);
    }

    @Override
    protected void teardown() {}

    @Override
    protected boolean isDoneImpl() {
	return true;
    }

    @Override
    protected void animate(float tpf) {}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
