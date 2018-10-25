package com.patchworkgalaxy.display.models;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.patchworkgalaxy.game.component.Weapon;
import com.patchworkgalaxy.general.util.Utils;

class AnimationWeapon extends Animation {
    
    
    private final Weapon _weapon;
    private final Model _weaponModel, _targetModel;
    private final Spatial _weaponSpatial, _targetSpatial;
    
    private boolean _missed;
    private WeaponStrategy _strategy;
    private boolean _launched;
    private static final float MAX_LIFETIME = 10;
    private float _lifetime;
    
    AnimationWeapon(Weapon weapon, Mode mode) {
	super(weapon.getShortName(), mode, null);
	_weapon = weapon;
	_weaponModel = weapon.getModel();
	_weaponSpatial = _weaponModel.getSpatial();
	_targetModel = weapon.getTarget().getModel();
	_targetSpatial = _targetModel.getSpatial();
	_lifetime = 0;
	_missed = weapon.getMissed();
    }
    
    private void setupStrategy() {
	if(_missed)
	    _strategy = new Miss(_weaponModel, _targetModel);
	else
	    _strategy = new Hit(_weaponModel, _targetModel);
    }
    
    private void setupPosition() {
	_weaponModel.setVisible(false);
	Quaternion rotation = spatial.getLocalRotation();
	Vector3f launchPos = spatial.getLocalTranslation()
	    .add(rotation.mult(attachment));

	_weaponModel.setPosition(launchPos);
	_weaponModel.setTarget(_strategy);
	_weaponModel.setVisible(true);
    }
    
    private void setupScale() {
	float mscale = model.getScale();
	mscale *= scale;
	//model.setScale(mscale);
    }
    
    @Override
    protected void initialize() {
	setupScale();
	setupPosition();
	setupStrategy();
	_weaponSpatial.setCullHint(CullHint.Always);
	_weapon.getTarget().dontAutoUpdate();
	model.setMoving(true);
    }

    @Override
    protected void teardown() {
	if(_launched)
	    _weaponModel.play("Death");
	_weapon.getTarget().updateAppearance();
	model.setMoving(false);
    }

    @Override
    protected boolean isDoneImpl() {
	if(!_launched) return false;
	if(_lifetime >= MAX_LIFETIME) return true;
	return _strategy.checkCollision();
    }

    @Override
    protected void animate(float tpf) {
	if(!_launched) {
	    setupPosition();
	    _weaponSpatial.setCullHint(CullHint.Inherit);
	    _weaponModel.play("Arrival");
	    _weaponModel.play("Thruster");
	    _launched = true;
	}
	float lifetime = _lifetime + tpf;
	if(_lifetime <= 1f && lifetime >= 1f)
	    model.setMoving(false);
	_lifetime = lifetime;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private static interface WeaponStrategy extends Positional {
	
	boolean checkCollision();
	
    }
    
    private static class Hit implements WeaponStrategy {

	private final Spatial _weaponSpatial, _targetSpatial;
	private final Vector3f _targetPoint;
	
	Hit(Model weaponModel, Model targetModel) {
	    _targetSpatial = targetModel.getSpatial();
	    _weaponSpatial = weaponModel.getSpatial();
	    //_targetPoint = targetModel.getPoint("Random");
	    _targetPoint = targetModel.getPositionVector();
	}
	
	@Override
	public Vector3f getPositionVector() {
	    return _targetPoint;
	}

	@Override
	public boolean checkCollision() {
	    return _targetSpatial.collideWith(
		_weaponSpatial.getWorldBound(), new CollisionResults()
		) > 0;
	}
	
    }
    
    private static class Miss implements WeaponStrategy {
	
	private final Vector3f _target;
	
	Miss(Model weaponModel, Model targetModel) {
	    
	    Vector3f launchPoint = weaponModel.getSpatial().getLocalTranslation();
	    Vector3f targetPoint = targetModel.getSpatial().getLocalTranslation();
	    
	    Vector3f offset = new Vector3f(
		    Utils.randUnitShifted(), Utils.randUnitShifted(), Utils.randUnitShifted()
		);
	    
	    if(offset.equals(Vector3f.ZERO))
		offset.y = 20;
	    else
		offset.multLocal(20);
	    
	    targetPoint.add(offset);
	    
	    Vector3f delta = targetPoint.subtract(launchPoint);
	    
	    float scale = 10000 / delta.length();
	    delta.multLocal(scale);
	    
	    _target = launchPoint.add(delta);
	}

	@Override
	public Vector3f getPositionVector() {
	    return _target;
	}

	@Override
	public boolean checkCollision() {
	    return false;
	}
	
    }
    
}