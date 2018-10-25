package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.game.commandcard.CommandCard;

final class CCButtonPlacer {
    
    private final Vector2f _origin, _backgroundCenter;
    private final Vector2f[] _buttonSlots;
    private final Vector2f[] _thermalBlockSlots;
    private final Vector2f _size;
    
    private static final float width =
	    (CCButton.FULL_WIDTH * Definitions.COMMAND_CARD_BUTTONS_PER_ROW) +
	    CCThermalBlock.FULL_WIDTH * Definitions.THERMAL_BLOCKS_PER_COLUMN;
    private static final float height =
	    CCButton.FULL_HEIGHT * 2 + CCThermalBlock.FULL_HEIGHT * 3;
    private static final float _originX =
	    (CCButton.FULL_WIDTH * Definitions.COMMAND_CARD_BUTTONS_PER_ROW) / -2;
    
    CCButtonPlacer(CommandCard commandCard) {
	_size = new Vector2f(width/2, height/2);
	_origin = new Vector2f(_originX, 0);
	_buttonSlots = buttonSlots();
	_thermalBlockSlots = tbSlots();
	_backgroundCenter = new Vector2f(_origin);
	_backgroundCenter.x += CCButton.FULL_WIDTH + CCThermalBlock.FULL_WIDTH;
	_backgroundCenter.y += CCThermalBlock.FULL_HEIGHT / 2;
    }
    
    private Vector2f[] buttonSlots() {
	Vector2f[] result = new Vector2f[6];
	for(int i = 0; i < 6; ++i)
	    result[i] = buttonSlot(i);
	return result;
    }
    
    private Vector2f[] tbSlots() {
	Vector2f[] result = new Vector2f[6];
	for(int i = 0; i < 6; ++i)
	    result[i] = tbSlot(i);
	return result;
    }
    
    private Vector2f buttonSlot(int index) {
	Vector2f result = new Vector2f(_origin);
	result.x += CCButton.FULL_WIDTH * (index % Definitions.COMMAND_CARD_BUTTONS_PER_ROW);
	result.y -= CCButton.FULL_HEIGHT * (index / Definitions.COMMAND_CARD_BUTTONS_PER_ROW);
	return result;
    }
    
    private Vector2f tbSlot(int index) {
	Vector2f result = buttonSlot(Definitions.COMMAND_CARD_BUTTONS_PER_ROW - 1);
	result.x += CCThermalBlock.FULL_WIDTH * 1.5f;
	result.x += CCThermalBlock.FULL_WIDTH * (index / Definitions.THERMAL_BLOCKS_PER_COLUMN);
	result.y += CCThermalBlock.FULL_HEIGHT * (index % Definitions.THERMAL_BLOCKS_PER_COLUMN);
	return result;
    }
    
    Vector2f getButtonOffset(int index) {
	return new Vector2f(_buttonSlots[index]);
    }
    
    Vector2f getTBoffset(int index) {
	return new Vector2f(_thermalBlockSlots[index]);
    }
    
    Vector2f getRowPosition(int index) {
	return getRowPosition0(index, 1);
    }
    
    Vector2f getNamePosition() {
	return getRowPosition0(2, 2).addLocal(-CCButton.FULL_WIDTH/2, 0);
    }
    
    Vector2f getCenter() {
	return new Vector2f(_backgroundCenter);
    }
    
    Vector2f getSize() {
	return new Vector2f(_size);
    }
    
    private Vector2f getRowPosition0(int index, int where) {
	Vector2f result;
	result = new Vector2f(getButtonOffset(where));
	result.y += (CCButton.FULL_HEIGHT + CCThermalBlock.FULL_HEIGHT) / 2;
	result.y += CCThermalBlock.FULL_HEIGHT * index;
	return result;
    }
    
}
