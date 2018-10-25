package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Ship;

class AnimationMove extends Animation {

    protected float _progress, _duration;
    protected Vector3f _initial, _target, _scratch;
    
    AnimationMove(String trigger, Mode mode) {
	super(trigger, mode, null);
    }
    
    @Override
    protected void initialize() {
	
        handleUndocking();
	_initial = new Vector3f(spatial.getLocalTranslation());
	_target = new Vector3f(model.getTarget());
	_scratch = new Vector3f();
	_duration = _initial.distance(_target) / model.getSpeed();
	model.setMoving(true);
        int hashcode = spatial.hashCode();
        try{  
                            for(int w=0;w<spatial.getParent().getQuantity();w++){
                            String shipname = spatial.getParent().getChild(w).getName();
                            if(shipname!=null)
                            if(shipname.equals("Reverse Fireball")){
                            ParticleEmitterReverseFireball reverseFireball =
                            (ParticleEmitterReverseFireball)spatial.getParent().getChild(w);
                            int shipHashCode = reverseFireball.getShipHashCode();
                            if(shipHashCode==hashcode){
                            MemoryOfShipHashCodes.FireballsForMoving.add(reverseFireball);
                            }}
                   
        }}catch(NullPointerException e){e.printStackTrace();}
    }

    @Override
    protected void teardown() {
	spatial.setLocalTranslation(_target);
	_progress = 1;
	model.setMoving(false);
	handleDocking();
        MemoryOfShipHashCodes.FireballsForMoving.clear();
    }

    @Override
    protected boolean isDoneImpl() {
	return _progress >= 1;
    }

    @Override
    protected void animate(float tpf) {
	tpf /= _duration;
	_progress += tpf;
	spatial.setLocalTranslation(
	    _scratch.interpolate(_initial, _target, _progress)
	);
        if(MemoryOfShipHashCodes.FireballsForMoving.size()>0){
            for(ParticleEmitterReverseFireball fireball
                    : MemoryOfShipHashCodes.FireballsForMoving){
                fireball.setLocalTranslation(
	    _scratch.interpolate(_initial, _target, _progress)
	);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private void handleDocking() {
	Ship ship = getShip();
	if(ship != null && ship.getHangarGraphic() && ship.getCarriedBy() != null) {
	    model.setVisible(false);
	}
    }
    
    private void handleUndocking() {
	Ship ship = getShip(); 
	if(ship != null && !ship.getHangarGraphic() && ship.getCarriedBy() != null) {
	    model.setPosition(ship.getCarriedBy().getPositionVector());
	    model.setVisible(true);
	}
    }
    
    private Ship getShip() {
	SelectionControl sc = model.getSpatial().getControl(SelectionControl.class);
	if(sc != null) {
	    GameComponent component = sc.getComponent();
	    if(component instanceof Ship)
		return (Ship)component;
	}
	return null;
    }
    
}
