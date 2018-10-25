package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.tile.Tile;

class TileTooltips {
    private TileTooltips() {}
    
    static void update(Tile tile, Component component, ShipSystem system) {
	ColoredText text = new ColoredText();
	writeSystem(tile, system, text);
	writeShip(tile, text);
	component.write(text, Property.TOOLTIP_TEXT);
    }
    
    private static void writeSystem(Tile tile, ShipSystem system, ColoredText text) {
	if(system == null || !system.canTarget(tile)) return;
	text.addText(system.getDisplayName());
	text.spliceOtherText(system.getTargetingTooltip(tile), "\n");
    }
    
    private static void writeShip(Tile tile, ColoredText text) {
	Ship ship = tile.getShip();
	if(ship != null) {
	    if(!text.toString().isEmpty())
		text.addText("\nTarget: ");
	    writeShipHeader(ship, text);
	    writeShipDefenses(ship, text);
	    writeShipTB(ship, text);
	}
    }
    
    private static void writeShipHeader(Ship ship, ColoredText text) {
	assert(ship != null);
	ColorRGBA iff = (GameAppState.isFriendly(ship)) ? ColorRGBA.Green : ColorRGBA.Red;
	text.addText(ship.getDisplayName(), iff);
	text.addText("\n");
    }
    
    private static void writeShipDefenses(Ship ship, ColoredText text) {
	assert(ship != null);
	int hull = ship.getHullIntegrity();
	int shield = ship.getShieldIntegrity();
	int maxHull = ship.getMaxHullIntegrity();
	int maxShield = ship.getMaxShieldIntegrity();
	float hullPct = (float)hull / (float)maxHull;
	float shieldPct = (float)shield / (float)maxShield;
	text.addText("Hull ");
	text.addText("[" + hull + "/" + maxHull + "]", getHullColor(hullPct));
	if(maxShield > 0) {
	    text.addText(" Shield ");
	    text.addText("[" + shield + "/" + maxShield + "]", getShieldColor(shieldPct));
	}
    }
    
    private static void writeShipTB(Ship ship, ColoredText text) {
	if(!GameAppState.isFriendly(ship) || ship.getMaxThermalLimit() == 0) return;
	int baseTB = ship.getThermalLimit(true, true);
	int engineTB = ship.getThermalLimit(false, true) - baseTB;
	int weaponTB = ship.getThermalLimit(true, false) - baseTB;
	text.addText(" " + baseTB + "TB", ColorRGBA.Yellow);
	if(engineTB > 0 || weaponTB > 0) {
	    text.addText(" ( ", ColorRGBA.Yellow);
	    if(engineTB > 0)
		text.addText(engineTB + "E", ColorRGBA.Yellow);
	    if(engineTB > 0 && weaponTB > 0)
		text.addText(", ", ColorRGBA.Yellow);
	    if(weaponTB > 0)
		text.addText(weaponTB + "W", ColorRGBA.Yellow);
	    text.addText(" )", ColorRGBA.Yellow);
	}
    }
    
    private static ColorRGBA getHullColor(float percent) {
	return new ColorRGBA((1 - percent), .8f * percent, 0, 1);
    }
    
    private static ColorRGBA getShieldColor(float percent) {
	return new ColorRGBA((1 - percent), percent, percent, 1);
    }
    
}
