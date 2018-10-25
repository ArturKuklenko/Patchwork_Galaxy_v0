package com.patchworkgalaxy.display.ui.defs.login;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardTextInputDescriptor;
import com.patchworkgalaxy.display.ui.util.action.CancelAction;
import com.patchworkgalaxy.display.ui.util.action.CompoundAction;
import com.patchworkgalaxy.display.ui.util.action.HotkeyAction;

class LoginTextInputCD extends StandardComponentDescriptor {
    
    private static final Vector2f ORIGIN = new Vector2f(0f, .15f);
    private static final Vector2f SIZE = new Vector2f(.4f, .05f);
    private static final Vector2f STEP = new Vector2f(0f, -.13f);
    
    static Vector2f getCenter(int y, int align) {
	if(align == 0)
	    return ORIGIN.add(STEP.mult(y));
	Vector2f center = new Vector2f(getCenter(y, 0));
	center.x += SIZE.x / 4;
	if(align < 0)
	    center.x *= -1;
	return center;
    }
    
    private static Vector2f getSize(int align) {
	return align == 0 ? SIZE : new Vector2f(SIZE).multLocal(new Vector2f(.45f, 1f));
    }
    
    LoginTextInputCD(String label, int y, int align, boolean isPassword) {
	super(label, getCenter(y, align), getSize(align));
	this
		.setBackgroundImage("Interface/pwgui/bars/progress_bar_background.png")
		.setZIndex(Definitions.Z_INDEX_MED + 50 - 6 * y)
		.setTextSize(20)
		.addTransition(.25f, Property.CENTER, Property.OPACITY)
		.addCallback(new CompoundAction(
		    new CancelAction(),
		    new HotkeyAction(Definitions.KEY_DUMMY)
		).asCallback(ComponentCallback.Type.SUBMIT))
		;
	StandardTextInputDescriptor input = new StandardTextInputDescriptor("  " + label + ": ", "");
	if(isPassword)
	    input.setPassword();
	setTextInputDescriptor(input);
    }
    
}
