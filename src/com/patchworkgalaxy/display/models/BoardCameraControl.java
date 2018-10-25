package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.patchworkgalaxy.game.tile.TileBoard;

public class BoardCameraControl extends AbstractControl {
    
    private final Vector3f _interpolateFrom, _interpolateTo, _scratch;
    
    private final Camera _camera;
    private float _timer;
    
    private static final Vector3f BOARD_OFFSET = new Vector3f(0, -100, 0);
    private static final Vector3f CAMERA_OFFSET
	    = new Vector3f(0, -1, 1f)
		.multLocal(350);
    
    private static final Vector3f PATCH_BOARD_OFFSET = new Vector3f(0, 40, 0);
    
    
    private boolean _inited, _started, _active, _patchMode;

    public BoardCameraControl(Camera camera) {
	_camera = camera;
	_interpolateFrom = new Vector3f();
	_interpolateTo = new Vector3f();
	_scratch = new Vector3f();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
	if(!_inited)
	    init();
	if(_active) {
	    if(_timer > 1)
		deactivate();
	    else
		interpolate();
	    _timer += tpf;
	}
    }
    
    private void init() {
	assert(!_inited);
	spatial.setLocalTranslation(camPos(Vector3f.ZERO));
	spatial.lookAt(camTarget(Vector3f.ZERO), Vector3f.UNIT_Y);
	_camera.setRotation(spatial.getLocalRotation());
	_inited = true;
    }
    
    private void interpolate() {
	assert(_timer >= 0 && _timer <= 1);
	_scratch.interpolate(_interpolateFrom, _interpolateTo, _timer);
	updateCam();
    }
    
    private void updateCam() {
	assert(_started);
	spatial.setLocalTranslation(_scratch);
	_camera.setLocation(spatial.getLocalTranslation());
    }
    
    public void lookAt(TileBoard board) {
	_patchMode = board.isPatchBoard();
	//if we haven't received our starting position from a previous call
	//to lookAt or lookAtInstantly, use lookAtInstantly to set up
	//our starting position
	if(!_started) {
	    lookAtInstantly(board);
	    _started = true;
	}
	else
	    activate(camPos(board.getOrigin()));
    }
    
    public void lookAtInstantly(TileBoard board) {
	_patchMode = board.isPatchBoard();
	_started = true;
	//we force the camera animation end to our target...
	_interpolateTo.set(camPos(board.getOrigin()));
	//and force the animation to end with the next control update tick
	_active = true;
	_timer = 1;
    }
    
    private void activate(Vector3f lookAt) {
	assert(_started);
	if(_active)
	    deactivate();
	_timer = 0;
	_interpolateTo.set(lookAt);
	_active = true;
    }
    
    private void deactivate() {
	_interpolateTo.set(_interpolateFrom);
	_interpolateFrom.set(_scratch);
	_active = false;
    }
    
    private Vector3f camPos(Vector3f vec3) {
	return vec3.add(CAMERA_OFFSET.add(getBoardOffset()));
    }
    
    private Vector3f camTarget(Vector3f vec3) {
	return vec3.add(getBoardOffset());
    }
    
    private Vector3f getBoardOffset() {
	return _patchMode ? PATCH_BOARD_OFFSET : BOARD_OFFSET;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
	super.setEnabled(enabled);
	
	if(!enabled)
	    deactivate();
	
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
}
