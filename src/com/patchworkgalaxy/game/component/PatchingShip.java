package com.patchworkgalaxy.game.component;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;

public class PatchingShip extends Ship {
    
    private ColoredText _description;
    
    public PatchingShip(GameProps props, Player owner, int eta) {
	super(props, owner);
	_description = new ColoredText();
	_description.addText(getDisplayName());
	_description.addText(" [Friendly]\n", ColorRGBA.Green);
	_description.addText("In Transit\n", ColorRGBA.Yellow);
	_description.addText("ETA: " + eta);
    }
    
    @Override
    public ColoredText getDescription() {
	return _description;
    }
    
    public void prioritize() {
	PatchingShip first = first();
	Tile priorityTile = priorityTile();
	setPosition(null);
	if(first != null)
	    first.deprioritize();
	setPosition(priorityTile);
    }
    
    private void deprioritize() {
	if(getPosition() == null)
	    return;
	PatchingShip next = next();
	if(next != null)
	    next.deprioritize();
	setPosition(getPosition().add(1, 0));
    }
    
    private Tile priorityTile() {
	return getPosition().add(-getPosition().x, 0);
    }
    
    private PatchingShip first() {
	Ship s = priorityTile().getShip();
	if(s != null)
	    return (PatchingShip)s;
	return null;
    }
    
    private PatchingShip next() {
	if(getPosition() == null)
	    return null;
	Tile tile = getPosition().add(2, 0);
	Ship s = tile.getShip();
	if(s != null)
	    return (PatchingShip)s;
	return null;
    }
    
    @Override public boolean isHeroic() {
	return false;
    }
    
    @Override public Vector3f getDefaultHeading() {
	return Definitions.PATCH_SPACE_SHIP_FACING;
    }
    
}
