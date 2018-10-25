package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.TeardownPanelAction;
import com.patchworkgalaxy.display.ui.util.action.WriteAction;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.general.util.SleepEffort;
import java.util.Collections;

class BottomBarPD extends StandardPanelDescriptor {
    
    private static final Vector2f CENTER = new Vector2f(0f, -.96f);
    private static final Vector2f SIZE = new Vector2f(1f, .04f);
    
    private final Action HIDE = new Action() {
	@Override public void act(Component actOn) {
	    actOn.write(0f, Property.OPACITY);
	    Efforts.submit(new SleepEffort(500))
		    .then(new TeardownPanelAction().asCallable(actOn));
	}
    };
    
    BottomBarPD(int x, int y) {
	String label = Localizer.getLocalizedString("ui.mainmenu.description." + (x + 1), y + 1 + "");
	setComponents(Collections.singleton(new StandardComponentDescriptor(null, CENTER, SIZE)
		.setText(new ColoredText(label))
		.setTextSize(14f)
		.setTextAlignCenter()
		.setTextVAlignCenter()
		.setBackgroundImage("Interface/pwgui/bars/background_bar1.png")
		.setOpacity(0f)
		.addTransition(.5f, Property.OPACITY)
		.addCallback(HIDE.asCallback(ComponentCallback.Type.UPDATE))
		.addCallback(new WriteAction(1f, Property.OPACITY).asCallback(ComponentCallback.Type.INITIALIZE))
		));
    }
    
}
