package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.game.commandcard.ThermalBlockType;
import com.patchworkgalaxy.display.ui.util.StandardTooltipDescriptor;

class CCThermalBlock extends CCComponentDescriptor {
    
    static final float WIDTH = Definitions.THERMAL_BLOCK_WIDTH;
    static final float HEIGHT = Definitions.THERMAL_BLOCK_HEIGHT;
    static final Vector2f SIZE = new Vector2f(WIDTH, HEIGHT);
    
    static final float FULL_WIDTH = 2 * (Definitions.THERMAL_BLOCK_PADDING_X + WIDTH);
    static final float FULL_HEIGHT = 2 * (Definitions.THERMAL_BLOCK_PADDING_Y + HEIGHT);
    
    CCThermalBlock(ThermalBlockType type, Vector2f center) {
	super(null, center, SIZE);
	this
		.setBackgroundImage(type.getIcon())
		.setZIndex(Definitions.Z_INDEX_HIGH + 50)
		.setTooltipDescriptor(new StandardTooltipDescriptor(type.getDescription()))
		;
    }
    
}
