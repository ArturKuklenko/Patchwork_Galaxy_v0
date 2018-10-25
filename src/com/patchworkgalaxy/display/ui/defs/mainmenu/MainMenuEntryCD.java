package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.TeardownPanelAction;
import com.patchworkgalaxy.display.ui.util.action.UpdateAllComponentsAction;
import com.patchworkgalaxy.display.ui.util.action.WriteAction;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.general.util.SleepEffort;

class MainMenuEntryCD extends StandardComponentDescriptor {
    
    private static final Vector2f CENTER = new Vector2f(-.02f, -.08f);
    private static final Vector2f STEP = new Vector2f(0f, -.1f);
    private static final Vector2f DIMENSIONS = new Vector2f(.2f, .05f);
    
    private final int _x, _y;
    private Panel _bottomBar;
    
    private static Vector2f getOrigin(int index) {
	Vector2f result = getCenter(index);
	result.x = 1 + DIMENSIONS.x;
	return result;
    }
    
    private static Vector2f getCenter(int index) {
	return CENTER.add(STEP.mult(index + 1));
    }
    
    private static Vector2f getDestination(int index) {
	Vector2f result = getCenter(index);
	result.x = -1 - DIMENSIONS.x;
	return result;
    }
    
    private static String getLabel(int x, int y) {
	return Localizer.getLocalizedString("ui.mainmenu." + (x + 1), String.valueOf(y + 1));
    }
    
    private Action HIDE = new Action() {
	@Override public void act(Component actOn) {
	    actOn.write(getDestination(_y), Property.CENTER);
	    Efforts.submit(new SleepEffort(1000))
		    .then(new TeardownPanelAction().asCallable(actOn));
	}
    };
    
    private final Action FOCUS = new Action() {
	@Override public void act(Component actOn) {
	    actOn.write(.8f, Property.OPACITY);
	    _bottomBar = UI.Instance.showPanel(new BottomBarPD(_x, _y));
	}
    };
    
    private final Action UNFOCUS = new Action() {
	@Override public void act(Component actOn) {
	    actOn.write(.5f, Property.OPACITY);
	    if(_bottomBar != null)
		new UpdateAllComponentsAction(_bottomBar).act(null);
	    _bottomBar = null;
	}
    };
    
    MainMenuEntryCD(int x, int y) {
	super(getLabel(x, y), getOrigin(y), DIMENSIONS);
	_x = x;
	_y = y;
	this
		.setBackgroundImage("Interface/pwgui/bars/bar_blue1.png")
		.setText(new ColoredText(getLabel(x, y)))
		.setTextAlignCenter()
		.setTextVAlignCenter()
		.setTextSize(16f)
		.setOpacity(.5f)
		.addTransition(.5f, Property.CENTER)
		.addTransition(.2f, Property.OPACITY)
		.addCallback(new WriteAction(getCenter(y), Property.CENTER).asCallback(ComponentCallback.Type.INITIALIZE))
		.addCallback(HIDE.asCallback(ComponentCallback.Type.UPDATE))
		.addCallback(FOCUS.asCallback(ComponentCallback.Type.MOUSE_IN))
		.addCallback(UNFOCUS.asCallback(ComponentCallback.Type.MOUSE_OUT))
		.addCallback(MainMenuClickActions.getAction(x, y).asCallback(ComponentCallback.Type.MOUSE_CLICK));
		;
    }
    
}
