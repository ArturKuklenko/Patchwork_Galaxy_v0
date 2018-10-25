package com.patchworkgalaxy.display.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class ModelControl extends AbstractControl {
    
    private final Model _model;
    
    ModelControl(Model model) {
	_model = model;
    }

    @Override protected void controlUpdate(float tpf) {}

    @Override protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    public Model getModel() {
	return _model;
    }
    
}
