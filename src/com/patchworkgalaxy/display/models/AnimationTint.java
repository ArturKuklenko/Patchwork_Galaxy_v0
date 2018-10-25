package com.patchworkgalaxy.display.models;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

class AnimationTint extends Animation {

    private float _timer = 1f;
    private final ColorRGBA _color;
    private final boolean _immediate;
    
    AnimationTint(String trigger, Mode mode, ColorRGBA color, boolean immediate) {
	super(trigger, mode, null);
	_color = color;
	_immediate = immediate;
    }
    
    @Override protected void initialize() {
	model.tint(_color, _immediate);
    }

    @Override protected void teardown() {}

    @Override protected boolean isDoneImpl() {
	return _timer < 0;
    }

    @Override protected void animate(float tpf) {
        System.out.print(" AnimTint");
	_timer -= tpf;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
