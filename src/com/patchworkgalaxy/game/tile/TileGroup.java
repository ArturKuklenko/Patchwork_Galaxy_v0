package com.patchworkgalaxy.game.tile;

import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Selectable;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import java.util.HashSet;
import java.util.Set;

/**
 * A set of tiles. A tile may be part of any number of groups. When a tile
 * checks an event, its tile groups also check that event. Strategic locations
 * are a special case of tile group.
 * @author redacted
 */
public class TileGroup extends GameComponent implements Selectable {

    final Tile anchor;
    final Set<Tile> tiles;
    Set<Ship> ships;
    Player owner;

    
    TileGroup(GameProps props, Tile anchor, Set<Tile> group) {
	super(props, Breed.TILEGROUP);
	this.anchor = anchor;
	this.tiles = group;
	this.ships = new HashSet<>();
	String[] conditions = props.getString("Condition").split(",");
	for(String condition : conditions)
	    addCondition(condition);
    }
    
    static TileGroup create(GameProps props, Tile anchor, Set<Tile> group) {
	if(props.getBoolean("Strategic!"))
	    return new TileGroupStrategic(props, anchor, group);
	else
	    return new TileGroup(props, anchor, group);
    }
    
    public static GameProps getStrategicProps() {
	return new MutableGameProps()
	    .set("Name", "Strategic Location")
	    .set("Condition", "condition:Strategic")
	    .set("Strategic!", true);
    }

    @Override
    public Tile getPosition() {
	return anchor;
    }

    @Override
    public Player getPlayer() {
	return owner;
    }
    
    @Override
    public GameState getGameState() {
	return anchor.getGameState();
    }

    @Override
    public ColoredText getDescription() {
	ColoredText text = new ColoredText();
	text.addText(getDisplayName());
	return text;
    }

    void checkTileGroup() {
	ships.clear();
	for(Tile tile : tiles) {
	    Ship ship = tile.getShip();
	    if(ship != null)
		ships.add(ship);
	}
    }

    /**
     * Determines if this group contains a particular tile.
     * @param tile the tile
     * @return if the tile is in this group
     */
    public boolean contains(Tile tile) {
	for(Tile t : tiles) {
	    if(t == tile)
		return true;
	}
	return false;
    }

    @Override
    public Object lookup(String name) {
	if(name.equals("center"))
	    return anchor;
	else
	    return super.lookup(name);
    }
    
    @Override
    public void onJoinGame() {
	super.onJoinGame();
	for(Tile tile : tiles)
	    tile.addToTileGroup(this);
    }
    
    //adolfnewStart
    @Override
    public double getAccuracyFalloff() {
        try {
            return anchor.getShip().getAccuracyFalloff();
        } catch (Exception e) {
            return 0;
        }
    }
    //adolfnewEnd
    
}
