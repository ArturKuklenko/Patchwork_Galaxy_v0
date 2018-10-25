package com.patchworkgalaxy.display.oldui;

import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.patchworkgalaxy.PatchworkGalaxy;
import java.util.ArrayList;
import java.util.List;

public class ChatLog extends AbstractControl {
    
    private final String _name;
    private int _nameSuffix;
    
    private ColoredText _waitingText;
    
    private final List<UX2DControl> _controls;
    private final int _size;
    private final float _duration;
    private final Vector2f _bottomCenter, _dimensions;
    
    private static UX2D ui = UX2D.getInstance();
    
    public ChatLog(String name, int size, Vector2f bottomCenter, Vector2f dimensions) {
	this(name, size, bottomCenter, dimensions, Float.NaN);
    }
    
    public ChatLog(String name, int size, Vector2f bottomCenter, Vector2f dimensions, float duration) {
	_name = name;
	_controls = new ArrayList<>();
	_size = size;
	_bottomCenter = bottomCenter;
	_dimensions = dimensions;
	_duration = duration;
    }
    
    public void enqueueText(String newtext) { 
	enqueueText(new ColoredText(newtext));
    }
    
    public void enqueueText(ColoredText newtext) {
	_waitingText = newtext;
    }
    
    public void addText(ColoredText newtext) {
	String name = _name + _nameSuffix;
	++_nameSuffix;
	UX2DControl text = ui.createControl(
		name,
		null,
		new ControlState()
		    .setDimensions(_dimensions)
		    .setCenter(_bottomCenter)
		    .setText(newtext)
		    .setZIndex(300)
	).setCallback(
	    CallbackType.TICK,
	    new UICallback() {
		float duration = _duration;
		@Override
		public void callback(UX2DControl control) {
		    duration -= control.getTPF();
		    if(duration < 0)
			control.setDead();
		}
	    }
	);
	text.controlUpdate(0);
	_controls.add(text);
	organizeControls();	
    }
    
    private void organizeControls() {
	int len = _controls.size();
	if(len > _size) {
	    _controls.remove(0).setDead();
	    --len;
	}
	float yOffset = 0;
	float totalHeight = _dimensions.y * 2;
	float width = _dimensions.x * PatchworkGalaxy.getDimensions().x;
	for(int i = len; --i >= 0;) {
	    UX2DControl control = _controls.get(i);
	    float controlHeight = (control.heightFromText(width, totalHeight) + 12) / PatchworkGalaxy.getDimensions().y;
	    control.changeState(
	        control.getDefaultState()
		    .setDimensions(new Vector2f(_dimensions.x, controlHeight))
		    .setCenter(_bottomCenter.add(new Vector2f(0f, yOffset + controlHeight / 2)))
	    );
	    
	    if(yOffset > totalHeight) {
		trimControlsTo(i);
		break;
	    }
	    
	    yOffset += controlHeight;
	}
    }
    
    private void trimControlsTo(int i) {
	int len = _controls.size();
	for(int delta = len - i; delta > 0; --delta)
	    _controls.remove(0).setDead();
    }

    @Override
    protected void controlUpdate(float tpf) {
	if(_waitingText != null) {
	    addText(_waitingText);
	    _waitingText = null;
	}    
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
