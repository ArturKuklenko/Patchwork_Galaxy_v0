package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;

/**
 * An event representing movement.
 * <p>
 * Move events act upon the sender. The recipient indicates the tile to
 * move to. Think of it like saying "Hey, Mom, mind if I move in with you?"
 * Except your mom is a tile.
 * </p>
 * @author redacted
 */
public class MoveEvent extends GameEvent {
    
    /**
     * Constructs a MoveEvent.
     * @param success success chance
     */
    public MoveEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	Tile moveTo = receiver.getPosition();
	Ship movingShip;
	if(sender instanceof Ship)
	    movingShip = (Ship)sender;
	else {
	    Tile moveFrom = sender.getPosition();
	    movingShip = moveFrom.getShip();
	}
	if(movingShip != null) {
	    boolean oldHangarGraphic = movingShip.getHangarGraphic();
	    boolean newHangarGraphic = (moveTo.getShip() != null);
	    movingShip.setHangarGraphic(newHangarGraphic);
	    if(!newHangarGraphic)
		movingShip.getModel().setVisible(true);
	    if(!movingShip.setPosition(moveTo))
		movingShip.setHangarGraphic(oldHangarGraphic); 
	}
    }

    @Override
    protected void cancelImpl() { }
    
    @Override
    protected void cancelImpl2() {}
    
}
