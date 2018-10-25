package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.ResetAction;
import com.patchworkgalaxy.general.lang.Localizer;

class MainMenuHeaderCD extends StandardComponentDescriptor {
    
    private static final Vector2f DIMENSIONS = new Vector2f(.24f, .12f);
    private static final Vector2f LEFTMOST = new Vector2f(-.75f, .63f);
    private static final Vector2f STEP = new Vector2f(.5f, 0f);
    
    private final Vector2f _originalCenter, _offsetCenter;
    private static Component _centered;
    
    private final Action FOCUS = new Action() {
	@Override public void act(Component actOn) {
	    if(_centered == actOn) return;
	    actOn.write(1f, Property.OPACITY);
	    actOn.write(_offsetCenter, Property.CENTER);
	}
    };
    
    private final Action UNFOCUS = new ResetAction() {
	@Override public void act(Component actOn) {
	    if(_centered == actOn) return;
	    super.act(actOn);
	}
    };
    
    private final Action CENTER = new Action() {
	@Override public void act(Component actOn) {
	    if(_centered != null)
		_centered.update(null, null);
	    if(_centered != actOn) {
		actOn.update(null, this);
		_centered = actOn;
	    }
	    else
		_centered = null;
	}
    };
    
    private static String getLabel(int index) {
	return Localizer.getLocalizedString("ui.mainmenu", String.valueOf(index + 1));
    }
    
    MainMenuHeaderCD(int index) {
	super(getLabel(index), LEFTMOST.add(STEP.mult(index)), DIMENSIONS);
	_originalCenter = LEFTMOST.add(STEP.mult(index));
	_offsetCenter = new Vector2f(_originalCenter).addLocal(0f, -.1f);
	this
		.setBackgroundImage("Interface/pwgui/buttons/button.png")
		.setText(new ColoredText(getLabel(index)))
		.setTextAlignCenter()
		.setTextVAlignCenter()
		.setTextSize(20f)
		.setOpacity(.8f)
		.addTransition(.4f, Property.CENTER)
		.addTransition(.2f, Property.OPACITY)
		.addCallback(FOCUS.asCallback(ComponentCallback.Type.MOUSE_IN))
		.addCallback(UNFOCUS.asCallback(ComponentCallback.Type.MOUSE_OUT))
		.addCallback(CENTER.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.addCallback(new ToggleMenuHeaderActions(index).asCallback(ComponentCallback.Type.UPDATE))
		;
    }
    
}
