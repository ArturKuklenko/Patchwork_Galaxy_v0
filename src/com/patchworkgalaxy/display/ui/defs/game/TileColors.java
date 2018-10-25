package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;

class TileColors {
    private TileColors() {}
    
    static void initialize(Tile tile) {
	tile.getModel().tint(ColorRGBA.BlackNoAlpha, true);
    }
    
    static void update(Tile tile, boolean selected, boolean highlight) {
	ColorRGBA color;
	if(!highlight) {
	    color = getBasisColor(tile);
	    augmentColor(color, tile);
	    checkSelection(color, tile, selected);
	}
	else
	    color = getHighlightColor(tile);
	tile.getModel().tint(color, false);
    }
    
    private static ColorRGBA getHighlightColor(Tile tile) {
	return new ColorRGBA(tile.isStrategic() ? ColorRGBA.White : ColorRGBA.LightGray);
    }
    
    private static void checkSelection(ColorRGBA color, Tile tile, boolean selected) {
	Ship ship = tile.getShip();
	if(ship != null && !selected) {
	    color.r *= .5f;
	    color.g *= .5f;
	    color.b *= .5f;
	}
    }
    
    private static void augmentColor(ColorRGBA color, Tile tile) {
	if(!tile.isStrategic() || tile.getShip() != null)
	    return;
	color.multLocal(tile.getStrategicGroup().getColorMultiplier());
	color.a = 1f;
    }
    
    private static ColorRGBA getBasisColor(Tile tile) {
	switch(tile.getAlignment()) {
	case FRIENDLY:
	    return getFriendlyColor(tile);
	case HOSTILE:
	    return getEnemyColor(tile);
	default:
	    return getNeutralColor(tile);
	}
    }
    
    private static ColorRGBA getFriendlyColor(Tile tile) {
	Ship ship = tile.getShip();
	if(ship != null) {
	    if(ship.getMaxThermalLimit() > 0) {
		if(ship.getThermalLimit(true, true) == 0 || !ship.hasSystemsAvailable())
		    return new ColorRGBA(ColorRGBA.Blue);
	    }
	}
	return new ColorRGBA(ColorRGBA.Green);
    }
    
    private static ColorRGBA getEnemyColor(Tile tile) {
	return new ColorRGBA(ColorRGBA.Red);
    }
    
    private static ColorRGBA getNeutralColor(Tile tile) {
	Player player = GameAppState.getLocalPlayer();
	if(player.hasArrivingShips() && tile.isValidPatchTileFor(player))
	    return new ColorRGBA(ColorRGBA.White);
	return new ColorRGBA(tile.isStrategic() ? ColorRGBA.Gray : ColorRGBA.DarkGray);
    }
    
}
