package com.patchworkgalaxy.game.component;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.models.Positional;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileBoard;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Resolver;


public class ShipDeployment extends GameComponent {

    private final Player _owner;
    private int _patchTime, _priority;
    private PatchingShip _patchingShip;
    
    public ShipDeployment(GameProps props, Player owner) {
	super(props, Breed.SHIP);
	_owner = owner;
	_patchTime = (int)(Resolver.createResolver(getGameState(), props.getString("PatchTime"), true)).toFloat(getGameState());
    }
    
    public int getBuildCost() {
        return props.getInt("BuildCost");
    }

    public int getPatchTime() {
        return _patchTime;
    }
    
    public int getPriority() {
	return _priority;
    }
    
    @Override
    public Player getPlayer() {
        return _owner;
    }
    
    public boolean patch(int progress) {
        _patchTime -= progress;
        if(_patchTime <= 0) {
            _patchTime = 0;
            return true;
        }
        return false;
    }
    
    public void createPatchingShip(Player player, TileBoard board, boolean playArrivalAnimation) {
	if(board == null) return;
	if(_patchingShip != null)
	    throw new IllegalStateException("Attempted to recreate a patch ship");
	_patchingShip = new PatchingShip(props, player, _patchTime);
	while(board.tileAtCoordinates(_priority, _patchTime).getShip() != null)
	    ++_priority;
	_patchingShip.setPosition(board.tileAtCoordinates(_priority, _patchTime));
	_patchingShip.onJoinGame();
	Vector3f where = _patchingShip.getModel().getPositionVector();
	_patchingShip.getModel().setTarget(new Positional.FromVector(where.add(Definitions.PATCH_SPACE_SHIP_FACING)));
	 
	if(playArrivalAnimation)
	    _patchingShip.getModel().play("Arrival");
    }
            
    public void writePatchingShip() {
	if(_patchingShip == null) return;
	_priority = _patchingShip.getPosition().x;
	_patchingShip.kill();
	_patchingShip.updateAppearance();
	_patchingShip = null;
    }

    @Override
    public Tile getPosition() {
	return _owner.getPosition();
    }

    @Override
    public double getAccuracyFalloff() {

    return _owner.getAccuracyFalloff();
    }
    
}
