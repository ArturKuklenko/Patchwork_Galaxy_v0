package com.patchworkgalaxy.display.models;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

class AnimationTurn extends Animation {

    private float _progress;
    private float _maneuverability;
    private Quaternion _initial, _target;
    private final Quaternion _scratch;
    
    private boolean _skip;
    
    AnimationTurn(String trigger, Mode mode, float maneuverability) {
	super(trigger, mode, null);
	_maneuverability = maneuverability;
	_scratch = new Quaternion();
    }
    
    @Override
    protected void initialize() {
	
	Vector3f mvec = model.getPositionVector();
	Vector3f tvec = model.getTarget();
	if(mvec.equals(tvec)) {
	    _skip = true;
	    return;
	}
	
	_initial = new Quaternion(spatial.getLocalRotation());
	spatial.lookAt(model.getTarget(), Vector3f.UNIT_Z);
	_target = new Quaternion(spatial.getLocalRotation());
	spatial.setLocalRotation(_initial);
	Vector3f v1 = _initial.getRotationColumn(0).normalizeLocal();
	Vector3f v2 = _target.getRotationColumn(0).normalizeLocal();
	float timemult = v1.angleBetween(v2) / (2 * FastMath.PI);
	_maneuverability /= timemult;
	
	if(Float.isInfinite(_maneuverability) || Float.isNaN(_maneuverability) || _maneuverability < 0)
	    _progress = 1;
    }

    @Override
    protected void teardown() {
	if(!_skip)
	    spatial.setLocalRotation(_target);
    }

    @Override
    protected boolean isDoneImpl() {
	return _progress >= 1;
    }

    @Override
    protected void animate(float tpf) {
	//occasionally a model will be told to face its own point,
	//if this happens we just ignore it
	if(_skip) {
	    end();
	    return;
	}
	
	//if our maneuverability is nonpositive, we immediately end the rotation...
	if(_maneuverability <= 0)
	    end();  //our teardown() implemenation sets the rotation to its final value
	
	tpf *= _maneuverability;
	_progress += tpf;
	spatial.setLocalRotation(
	    _scratch.slerp(_initial, _target, _progress)
	);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
