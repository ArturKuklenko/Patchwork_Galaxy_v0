package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.game.commandcard.CommandCard;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.action.Action;

class CCHealthBar extends CCComponentDescriptor {
    
    private final boolean _isShield;
    private final CommandCard _commandCard;
    private final float _width;
    
    //below this width, we start losing parts of the label off to the sides
    private static final float MINIMUM_POSITIVE_WIDTH = CCButton.FULL_WIDTH * 3 / 8;
    
    private final Action _update = new Action() {
	@Override public void act(Component actOn) {
	    actOn.write(getCurrentSize(), Property.SIZE);
	    actOn.write(getLabel(), Property.TEXT);
	    actOn.write(BitmapFont.Align.Center, Property.TEXT_ALIGN);
	}
    };
    
    CCHealthBar(CommandCard commandCard, Vector2f center, int widthMultiplier, boolean isShield) {
	super(null, center, new Vector2f(
		getWidth(getBaseWidth(widthMultiplier), commandCard, isShield),
		CCThermalBlock.HEIGHT));
	_width = getBaseWidth(widthMultiplier);
	_commandCard = commandCard;
	_isShield = isShield;
	this
		.setBackgroundImage(isShield ? Definitions.SHIELD_BAR_IMAGE : Definitions.HULL_BAR_IMAGE)
		.addCallback(_update.asCallback(ComponentCallback.Type.UPDATE))
		.addTransition(1, Property.SIZE)
		.setZIndex(Definitions.Z_INDEX_HIGH + 50)
		.setText(getLabel())
		.setTextAlignCenter()
		;
    }
    
    private static float getBaseWidth(float multiplier) {
	return CCButton.FULL_WIDTH * multiplier / 2;
    }
    
    private static float getWidth(float base, CommandCard commandCard, boolean isShield) {
	float pct = (isShield ? commandCard.getShieldPct() : commandCard.getHullPct());
	float result = pct * base;
	if(result > 0 && result < MINIMUM_POSITIVE_WIDTH)
	    result = MINIMUM_POSITIVE_WIDTH;
	return result;
    }
    
    private Vector2f getCurrentSize() {
	float actualWidth = _width * (_isShield ? _commandCard.getShieldPct() : _commandCard.getHullPct());
	if(actualWidth > 0 && actualWidth < MINIMUM_POSITIVE_WIDTH)
	    return new Vector2f(MINIMUM_POSITIVE_WIDTH, CCThermalBlock.HEIGHT);
	return new Vector2f(actualWidth, CCThermalBlock.HEIGHT);
    }
    
    private ColoredText getLabel() {
	int current = _isShield ? _commandCard.getCurrentShield(): _commandCard.getCurrentHull();
	if(current <= 0) return new ColoredText();
	int max = _isShield ? _commandCard.getMaxShield() : _commandCard.getMaxHull();
	return new ColoredText().addText("[" + current + "/" + max + "]", ColorRGBA.Black);
    }
    
    
}
