package com.patchworkgalaxy.game.tile;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.RenderableComponent;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.MutableGameProps;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A tile on the game board.
 * <p>This class is responsible for tracking all information about the tile,
 * including what ship is on it and any tile groups it's part of.</p>
 * @author redacted
 */
public final class Tile extends GameComponent implements RenderableComponent {
    
    public final int x, y;
    
    private final TileBoard _board;
    private final Tile[] _adjacency;
    private Ship _presentShip;
    private final Set<TileGroup> _tileGroups;
    private TileGroupStrategic _strategicGroup;
    
    Tile(int x, int y, TileBoard board) {
	super( 
	    new MutableGameProps()
		.set(
		    "Name",
		    new StringBuilder()
			.append("Tile(").append(x).append(", ").append(y).append(")")
			.toString()
		).set(
		    "Flags",
		    0
		), Breed.TILE
	);
        this.x = x;
        this.y = y;
        _board = board;
	_tileGroups = new HashSet<>();
	_adjacency = new Tile[6];
    }
    
    void updateAdjacency(Set<Tile> needsUpdate) {
	if(!needsUpdate.contains(this))
	    return;
	needsUpdate.remove(this);
	for(int i = 0; i < 6; ++i)
	    updateAdjacency(needsUpdate, i);
    }
    
    private void updateAdjacency(Set<Tile> needsUpdate, int i) {
	int dx, dy;
	int right = y & 1;
	int left = right - 1;
	switch(i) {
	case 0:
	    dx = -1;
	    dy = 0;
	    break;
	case 1:
	    dx = 1;
	    dy = 0;
	    break;
	case 2:
	    dx = right;
	    dy = -1;
	    break;
	case 3:
	    dx = left;
	    dy = 1;
	    break;
	case 4:
	    dx = right;
	    dy = 1;
	    break;
	case 5:
	    dx = left;
	    dy = -1;
	    break;
	default:
	    throw new IllegalArgumentException();
	}
	Tile current = _adjacency[i];
	Tile updated = add(dx, dy);
	if(current != updated) {
	    assert(current == null);
	    _adjacency[i] = updated;
	    if(updated != null)
		updated.updateAdjacency(needsUpdate);
	}
    }
    
    public Set<Tile> getAdjacency() {
	Set<Tile> set = new HashSet<>(6);
	for(Tile tile : _adjacency)
	    if(tile != null)
		set.add(tile);
	return set;
    }
    
    public boolean isAdjacent(Tile other) {
	return Arrays.asList(_adjacency).contains(other);
    }
    
    public Ship getShip() {
        if(_presentShip != null) {
            if(!equals(_presentShip.getPosition()) || !_presentShip.alive())
                _presentShip = null;
        }
        return _presentShip;
    }
    
    /**
     * Sets the ship at this tile.
     * <p>
     * This method performs some additional validation to make sure there
     * isn't already a ship here, as there can be only one ship on a given tile.
     * </p><p>Furthermore, this method also makes sure that the ship agrees that
     * this should be its position. As such, it's a bad idea to call this method
     * directly - use {@link Ship#setPosition(Tile)} instead.
     * </p>
     * @param ship The ship to place here.
     * @throws IllegalStateException if there's a different ship here.
     * @throws IllegalArgumentException if the ship thinks it should be elsewhere
     */
    public void setShip(Ship ship) {
        if(getShip() != null && ship != null)
	    throw new IllegalStateException("Tried to set a ship to tile (" + x + "," + y + "), but there's already a ship there");
        if(ship != null && ship.getPosition() != this)
		throw new IllegalArgumentException("Tried to set a ship to tile (" + x + "," + y + "), but it has a different position");
	if(_presentShip != null)
	    _presentShip.update();
	_presentShip = ship;
	update();
	updateTileGroups();
	if(_presentShip != null)
	    _presentShip.update();
    }
    
    void updateTileGroups() {
	for(TileGroup tg : _tileGroups)
	    tg.checkTileGroup();
    }
    
    @Override
    public int hashCode() {
        return (y & 255) + ((x & 255) << 8);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        final Tile other = (Tile)obj;
        boolean equals = _board == other._board && this.x == other.x && this.y == other.y;
	return equals;
    }
    
    public Tile add(int dx, int dy) {
        return _board.tileAtCoordinates(x + dx, y + dy);
    }
    
    /**
     * Returns the range to another tile.
     * @param b The other tile.
     * @return The range.
     */
    public int range(Tile b) {
	if(b == null)
	    throw new NullPointerException("Can't find the range to a null tile");
	if(!_board.equals(b._board))
	    throw new IllegalArgumentException("Can't find the range to tiles on different boards");
	return TileCollector.rangefinder(b, this);
    }
    
    /**
     * Computes the TB cost for some ship to travel from this tile to some other
     * tile. This calculation considers relevant obstacles, such as opposing
     * ships, and abilities of the ship, such as evasive movement.
     * <p>
     * The {@code capFromTB} argument indicates that, if the pathing cost
     * exceeds the ship's movement range, the ship's movement range plus one 
     * should be returned instead. In most cases, all values that exceed
     * the ship's movement range are equivalent (if the ship can't reach a tile,
     * you probably don't care by how much it can't reach it), and this can
     * represent a significant optimization, so it should almost always be true.
     * </p><p>
     * This method is used primarily for ships undocking. For general-use ship
     * pathing, consider {@link Tile#pathingCostForShip(Tile, boolean), which
     * assumes the pathing ship is the one at this tile. If the ship is
     * currently docked, there is a 1 TB discount on the movement.
     * </p>
     * @param ship the pathing ship
     * @param other the tile to path to
     * @param capFromTB cap the cost at the ship's movement TB?
     * @return the pathing cost
     */
    public int pathingCostForShip(Ship ship, Tile other, boolean capFromTB) {
	if(ship == null)
	    throw new IllegalStateException("Attempted to compute pathing cost for null ship");
	int max = capFromTB ? ship.getSpeed() + 1 : Integer.MAX_VALUE;
	int cost = new TileCollector(this, max,
	    TileFilter.canPath(ship), 
	    TileFilter.isTile(other)
	).getDistance();
	return cost;
    }
    /**
     * Computes the TB cost for this tile's ship to move to some other tile.
     * This calculation considers relevant obstacles, such as opposing
     * ships, and abilities of the ship, such as evasive movement.
     * <p>
     * The {@code capFromTB} argument indicates that, if the pathing cost
     * exceeds the ship's movement range, the ship's movement range plus one 
     * should be returned instead. In most cases, all values that exceed
     * the ship's movement range are equivalent (if the ship can't reach a tile,
     * you probably don't care by how much it can't reach it), and this can
     * represent a significant optimization, so it should almost always be true.
     * </p><p>
     * This method is a convenience overload of
     * {@link Tile#pathingCostForShip(Ship, Tile, boolean)} which assumes the
     * pathing ship is the ship currently at this tile.
     * </p>
     * @param other the tile to path to
     * @param capFromTB cap the cost at the ship's movement TB?
     * @return the pathing cost
     */
    public int pathingCostForShip(Tile other, boolean capFromTB) {
	return pathingCostForShip(_presentShip, other, capFromTB);
    }
    
    @Override
    public Tile getPosition() {
        return this;
    }
    
    private float getRenderX() {
	int centered = x - _board.getMapWidth() / 2;
	float offset = centered * TileBoard.TILE_WIDTH;
	if((y & 1) == 0)
	    offset -= TileBoard.TILE_WIDTH / 2;
	return _board.getOrigin().x + offset;
    }
    
    private float getRenderY() {
	int center = _board.getMapHeight() / 2;
	int offset = y - center;
        return _board.getOrigin().y + (float)offset * TileBoard.TILE_HEIGHT;
    }
    
    private float getRenderZ() {
	return _board.getOrigin().z;
    }
    
    @Override
    public Vector3f getPositionVector() {
	return new Vector3f(getRenderX(), getRenderY(), getRenderZ());
    }
    
    public Vector3f getOffsetPosition() {
	return getPositionVector().add(0, 0, 20);
    }
    
    /**
     * @deprecated Stupid.
     */
    @Deprecated
    public boolean isValidPatchTileFor(Player player) {
	//this is shit
	int dx = x - player.getHeadquarters().getPosition().x;
	int dy = y - player.getHeadquarters().getPosition().y;
	if(dx > 1)
	    return false;
	if((dy & 1) == 0)
	    ++dx;
	if(dx < 0)
	    return false;
        return getShip() == null;
    }
    
    
    void addToTileGroup(TileGroup group) {
	_tileGroups.add(group);
	if(group instanceof TileGroupStrategic)
	    _strategicGroup = (TileGroupStrategic)group;
    }
    
    public boolean isStrategic() {
	return _strategicGroup != null;
    }
    
    public TileGroupStrategic getStrategicGroup() {
	return _strategicGroup;
    }
    
    @Override
    public GameState getGameState() {
	return _board.getGameState();
    }
    
    /**
     * Checks that this is part of a valid board.
     * @return true if the board is valid, false otherwise
     * @see TileBoard#destroy
     */
    @Override
    public boolean graphicValid() {
        System.out.println("Tile graphicValid");
	return super.graphicValid() && _board != null && _board.isValid();
    }

    @Override
    public Object lookup(String name) {
	switch (name) {
	case "strategic":
	    return _strategicGroup;
	case "ship":
	    return getShip();
	default:
	    Object o = null;
	    if(getShip() != null)
		o = _presentShip.lookup(name);
	    if(o == null)
		return super.lookup(name);
	    else
		return o;
	}
    }
    
    public TileBoard getBoard() {
	return _board;
    }
    
    public boolean isPatchTile() {
	return _board.isPatchBoard();
    }

    @Override
    public Player getPlayer() {
	if(getShip() != null)
	    return _presentShip.getPlayer();
	else if(_strategicGroup != null)
	    return _strategicGroup.getPlayer();
	else
	    return null;
    }
    
    @Override
    public String getModelName() {
	return "Hex";
    }
    
    @Override
    public boolean checkEvent(GameEvent gameEvent, boolean outgoing) {
	for(TileGroup tg : _tileGroups)
	    tg.checkEvent(gameEvent, outgoing);
	return super.checkEvent(gameEvent, outgoing);
    }
    
    @Override
    public void onJoinGame() {
	super.onJoinGame();
	showGraphic();
	//getModel().play("Tile");
    }
    
    @Override
    public String toString() {
	return new StringBuilder(isPatchTile() ? "Patch" : "")
	    .append("Tile(")
	    .append(x).append(", ").append(y).append(")")
	    .toString();
    }
    
    public GameComponent asTarget() {
	return _presentShip == null ? this : _presentShip;
    }

    @Override
    public double getAccuracyFalloff() {
        return _presentShip.getAccuracyFalloff(); //To change body of generated methods, choose Tools | Templates.
    }
    
}