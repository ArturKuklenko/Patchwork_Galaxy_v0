package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;

public class MainMenuPD extends StandardPanelDescriptor {
    
    private static final String LABEL =
	    "P  a  t  c  h  w  o  r  k       G    a    l    a    x    y";
    
    public MainMenuPD() {
	super(
		new StandardComponentDescriptor(new Vector2f(0f, .85f), new Vector2f(1f, .05f))
		    .setText(new ColoredText(LABEL, ColorRGBA.Yellow))
		    .setTextAlignCenter()
		    .setTextSize(30)
		,
		new MainMenuHeaderCD(0),
		new MainMenuHeaderCD(1),
		new MainMenuHeaderCD(2),
		new MainMenuHeaderCD(3));
    }
    
}
