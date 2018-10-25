package com.patchworkgalaxy.game.tile;

import com.patchworkgalaxy.game.component.Ship;

public abstract class TileFilter {
    
    private TileFilter() {}
    
    abstract boolean filter(Tile tile);
    	
    public static TileFilter isTile(final Tile check) {
	return new TileFilter() {
	    @Override 
	    public boolean filter(Tile tile) {
		return tile.equals(check);
	    }
	};
    }
    public static TileFilter canPath(final Ship ship) {
	return new TileFilter() {
	    @Override
	    public boolean filter(Tile tile) {
		return ship.canPathThrough(tile);
	    }
	};
    }
    
}
