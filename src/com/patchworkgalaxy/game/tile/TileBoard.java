package com.patchworkgalaxy.game.tile;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.util.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileBoard {
    
    private final GameState _gameState;
    
    private final List<List<Tile>> _tiles;
    private boolean _dead;
    private final boolean _isPatchBoard;
    
    static final int TILE_WIDTH = 52, TILE_HEIGHT = 40;
    private final Vector3f _origin;
    
    private int _displayStatus = -1;
    
    public TileBoard(GameState gameState) {
	this(gameState, Vector3f.ZERO, false);
    }
    
    public TileBoard(GameState gameState, Vector3f origin, boolean patchBoard) {
	_gameState = gameState;
	_tiles = new ArrayList<>();
	_origin = origin;
	_isPatchBoard = patchBoard;
    }
    
    public void rectangle(int width, int height) {
	for(int x = 0; x < width; ++x) {
	    for(int y = 0; y < height; ++y){
		makeTile(x, y);
	    }
	}
	updateAdjacencies();
	display();
    }
    
    public void testmap() {
	for(int y = 0; y < 11; ++y) {
	    for(int x = 1; x < 13; ++x) {
		if(x == 12 && (y & 1) != 0)
		    continue;
		makeTile(x, y);
	    }
	}
	makeTile(0, 5);
	makeTile(12, 5);
	updateAdjacencies();
	display();
	GameProps strategic = TileGroup.getStrategicProps();
	Tile center = tileAtCoordinates(6, 5);
	Tile top = tileAtCoordinates(6, 1);
	Tile bottom = tileAtCoordinates(6, 9);
	createTileGroup(strategic, center, 1);
	createTileGroup(strategic, top, 1);
	createTileGroup(strategic, bottom, 1);
    }
    
    private List<Tile> implyColumn(int x) {
	if(Utils.rightpad(_tiles, x)) {
	    List<Tile> result = new ArrayList<>();
	    _tiles.set(x, result);
	    return result;
	}
	else {
	    List<Tile> list = _tiles.get(x);
	    if(list == null) {
		list = new ArrayList<>();
		_tiles.set(x, list);
	    }
	    return list;
	}
    }
    
    public Tile makeTile(int x, int y) {
	List<Tile> column = implyColumn(x);
	if(Utils.rightpad(column, y)) {
	    Tile result = new Tile(x, y, this);
	    column.set(y, result);
	    if(visible())
		result.onJoinGame();
	    return result;
	}
	else {
	    Tile tile = column.get(y);
	    if(tile == null) {
		tile = new Tile(x, y, this);
		column.set(y, tile);
	    }
	    return tile;
	}
    }
    
    public void display() {
	int height = 0;
	List<Tile> alltiles = new ArrayList<>();
	for(List<Tile> list : _tiles) {
	    if(list == null)
		continue;
	    for(Tile tile : list) {
		if(tile != null)
		    alltiles.add(tile);
	    }
	    height = Math.max(height, list.size());
	}
	_displayStatus = height;
	for(Tile tile : alltiles)
	    tile.onJoinGame();
    }
    
    public int getMapWidth() {
        return _tiles.size();
    }
    
    public int getMapHeight() {
	if(visible())
	    return _displayStatus;
	return _tiles.get(0).size();
    }
    
    /**
     * Gets the tile on this board at the indicated coordinates. This method
     * doesn't assume that tile exists, and will return null if it doesn't.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return the (possibly null) tile at those coordinates
     */
    public final Tile tileAtCoordinates(int x, int y) {
	if(x < _tiles.size() && x >= 0 && y >= 0) {
	    List<Tile> column = _tiles.get(x);
	    if(y < column.size())
		return column.get(y);
	}
	return null;
    }
    
    /**
     * Creates a tile group on this board.
     * @param props a GameProps object
     * @param anchor the center of the tile group
     * @param radius the radius of the tile group
     */
    public TileGroup createTileGroup(GameProps props, Tile anchor, int radius) {
	Set<Tile> tiles = new TileCollector(anchor, radius).getTiles();
	TileGroup tg = TileGroup.create(props, anchor, tiles);
	tg.onJoinGame();
	return tg;
    }
    
    public void destroy() {
	_dead = true;
    }
    
    public boolean isValid() {
	return !_dead;
    }
    
    public Vector3f getOrigin() {
	return _origin;
    }
    
    GameState getGameState() {
	return _gameState;
    }
    
    public boolean isPatchBoard() {
	return _isPatchBoard;
    }
    
    public void updateAdjacencies() {
	Set<Tile> needsUpdate = new HashSet<>();
	for(Iterable<Tile> i : _tiles) {
	    for(Tile j : i)
		if(j != null)
		    needsUpdate.add(j);
	}
	while(!needsUpdate.isEmpty())
	    needsUpdate.iterator().next().updateAdjacency(needsUpdate);
    }
    
    public Set<Tile> getAllTiles() {
	Set<Tile> result = new HashSet<>();
	for(List<Tile> i : _tiles)
	    result.addAll(i);
	return result;
    }
    
    private boolean visible() {
	return _displayStatus >= 0;
    }
    
}