package com.patchworkgalaxy.display.models;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

class Tinter extends AbstractControl {
    
    private ColorRGBA _from, _to, _scratch;
    private Material _material;
    private boolean _active;
    private float _timer;
    
    private static final float END_TIMER = 1;
    
    @Override public void setSpatial(Spatial spatial) {
	super.setSpatial(spatial);
	_from = new ColorRGBA(ColorRGBA.BlackNoAlpha);
	_to = new ColorRGBA(ColorRGBA.BlackNoAlpha);
	_scratch = new ColorRGBA(ColorRGBA.BlackNoAlpha);
	_material = ((Geometry)spatial).getMaterial();
    }
    
    void tint(ColorRGBA color, boolean immediate) {
	if(immediate)
	    tintImmediate(color);
	else
	    tintRegular(color);
    }
    
    private void tintRegular(ColorRGBA color) {
	_active = true;
	_timer = 0;
	_from.set(_scratch);
	_to.set(color);
    }
    
    private void tintImmediate(ColorRGBA color) {
	_active = true;
	_timer = END_TIMER;
	_from.set(color);
	_to.set(color);
	_scratch.set(color);
    }

    @Override protected void controlUpdate(float tpf) {
	if(!_active) return;
	_timer += tpf;
	if(_timer > END_TIMER) {
	    _scratch.set(_to);
	    _from.set(_to);
	    _active = false;
	}
	else
	    _scratch.interpolate(_from, _to, _timer);
	_material.setColor("Diffuse", _scratch);
    }

    @Override protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
