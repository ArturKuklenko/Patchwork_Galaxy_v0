package com.patchworkgalaxy.display.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.patchworkgalaxy.game.component.GameComponent;

public class SelectionControl extends AbstractControl {

    private final GameComponent _component;
    
    public SelectionControl(GameComponent component) {
	_component = component;
    }
    
    public GameComponent getComponent() {
	return _component;
    }
    
    @Override
    protected void controlUpdate(float tpf) {}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
