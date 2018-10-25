package com.patchworkgalaxy.display.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

class AnimationSupport extends Animation {
    
    private final AnimationDef[] _animationdefs;
    private final AnimationDef _supportdef;
    
    private Animation[] _animations;
    private Animation _supports;
    
    AnimationSupport(String trigger, Mode mode, AnimationDef supports, AnimationDef[] animations) {
	super(trigger, mode, null);
	_animationdefs = animations;
	_supportdef = supports;
    }
    
    @Override
    protected void initialize() {
	_animations = new Animation[_animationdefs.length];
	_supports = _supportdef.getAnimation(model);
	_supports.start();
	for(int i = 0; i < _animations.length; ++i) {
	    _animations[i] = _animationdefs[i].getAnimation(model);
	    _animations[i].start();
	}
    }

    @Override
    protected void teardown() {
	for(Animation animation : _animations)
	    animation.end();
	_supports.end();
    }

    @Override
    protected boolean isDoneImpl() {
	return _supports.isDone();
    }

    @Override
    protected void animate(float tpf) {}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
