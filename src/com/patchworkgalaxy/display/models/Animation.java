package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;
import com.jme3.scene.control.AbstractControl;
import com.patchworkgalaxy.game.component.Ship;

public abstract class Animation extends AbstractControl implements Cloneable {
    
    protected Model model;
    protected final Mode mode;
    private boolean _started, _specialed, _ended;
    private final String _trigger;
    
    protected Vector3f attachment;
    protected String pointname;
    protected float scale;
    protected Ship ship;
    
    Animation(String trigger, Mode mode, Ship ship) {
	this.mode = mode;
	_trigger = (trigger == null ? "" : trigger);
        this.ship = ship;
    }
    
    protected abstract void initialize();
    
    protected abstract void teardown();
    
    protected abstract void animate(float tpf);
    
    protected abstract boolean isDoneImpl();
    
    public enum Mode {
	STANDARD, PRIORITY, BACKGROUND;
    }
    
    void start() {
	if(model.getSpatial().getParent() == null) return;
	if(_started)
	    throw new IllegalStateException();
	model.getSpatial().addControl(this);
	attachment = new Vector3f(model.getPoint(pointname));
	Triggers.handleTrigger(this, true);
	initialize();
	_started = true;
    }
    
    void end() {
	if(model.getSpatial().getParent() == null) return;
	if(!_started)
	    throw new IllegalStateException();
	if(_ended) return;
	special();
	teardown();
	Triggers.handleTrigger(this, false);
	spatial.removeControl(this);
	_ended = true;
        //getOutReverseFireball();
    }
    void getOutReverseFireball(){
        try{
          for(int i=0;i<spatial.getParent().getQuantity();i++){
             spatial.getParent().detachChildNamed("Reverse Fireball");
          }
          }catch(NullPointerException e){}
    }
    void special() {
	if(!_specialed) {
	    Triggers.handleSpecialTrigger(this);
	    _specialed = true;
	}
    }
    
    @Override
    protected void controlUpdate(float tpf) {
	if(isDone()){
	    end();
        }
	else if(notBlocked() && _started && !_ended){
	    animate(tpf);
        }
    }
    
    private boolean notBlocked() {
	if(mode == Mode.STANDARD && model.blocking())
	    return false;
	return true;
    }
    
    boolean isDone() {
	return _ended || !model.isVisible() || isDoneImpl();
    }
    
    @Override
    protected Animation clone() {
	try {
	    Animation a = (Animation)super.clone();
	    a.spatial = null;
	    return a;
	}
	catch(CloneNotSupportedException e) {
	    throw new AssertionError(e);
	}
    }
    
    protected Animation withModel(Model model) {
	Animation a = clone();
	a.model = model;
	return a;
    }
    
    String getTrigger() {
	return _trigger;
    }
    
}
