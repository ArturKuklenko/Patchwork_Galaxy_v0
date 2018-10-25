package com.patchworkgalaxy.game.tile;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.event.CaptureEvent;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.parser.CanBeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TileGroupStrategic extends TileGroup {
    
    private static final Stage STAGE_ZERO
	    = new Stage(2, 0, 1f)
	    .next(8, 1, .5f)
	    .next(21, 2, .8f)
	    .next(28, 3, 1f)
	    .next(Integer.MAX_VALUE, 5, 1f)
	    .first();
	    
    private boolean _contested, _beingReverted;
    final StageTracker _stageTracker;
    
    TileGroupStrategic(GameProps props, Tile anchor, Set<Tile> group) {
	super(props,anchor, group);
	_stageTracker = new StageTracker(STAGE_ZERO);
    }
    
    public void capture(CaptureEvent e) {
	if(_contested)
	    e.cancel();
	else {
	    int m = e.getMagnitude();
	    Player p = e.getPlayer();
	    if(m > 0) {
		_beingReverted = (owner != null && p != owner);
		List<Stage> updates = _stageTracker.points(m, _beingReverted);
		for(Stage update : updates)
		    handleUpdate(update, p);
	    }
	    if(_beingReverted)
		e.cancel();
	}
    }
    
    private void handleUpdate(Stage update, Player p) {
	if(_beingReverted) {
	    if(owner == null)
		return;
	    Object reward = getGameState().lookup("event:Location Reversion " + (update.id + 1));
	    if(reward instanceof CanBeEvent)
		((CanBeEvent)reward).toEvent(this, p).enqueue();
	    if(update.id == 0) {
		owner = null;
		_beingReverted = false;
	    }
	}
	else { 
	    owner = p;
	    Object reward = getGameState().lookup("event:Location Stage " + update.id);
	    if(reward instanceof CanBeEvent)
		((CanBeEvent)reward).toEvent(this, owner).enqueue();
	}
    }
    
    @Override void checkTileGroup() {
	super.checkTileGroup();
	if(ships.isEmpty()) {
	    _contested = false;
	    return;
	}
	if(owner != null) {
	    boolean ownerPresent = false, rivalPresent = false;
	    for(Ship ship : ships) {
		if(ship.getPlayer() == owner)
		    ownerPresent = true;
		else
		    rivalPresent = true;
	    }
	    _contested = (ownerPresent && rivalPresent);
	    if(rivalPresent && !ownerPresent)
		_beingReverted = true;
	}
	else {
	    Player p = null;
	    for(Ship ship : ships) {
		if(p == null)
		    p = ship.getPlayer();
		else if(p != ship.getPlayer()) {
		    p = null;
		    break;
		}
	    }
	    _contested = (p == null);
	}
    }
    
    @Override
    public ColoredText getDescription() {
	ColoredText text = super.getDescription();
	writeIff(text);
	writeStage(text);
	writeProgress(text);
	writeFormation(text);
	return text;
    }
    
    private void writeIff(ColoredText text) {
	text.addText("\n");
	if(owner == null)
	    text.addText("[Neutral]", ColorRGBA.Yellow);
	else if(Player.isLocal(owner))
	    text.addText("[Friendly]", ColorRGBA.Green);
	else
	    text.addText("[Hostile]", ColorRGBA.Red);
    }
    
    private void writeStage(ColoredText text) {
	text.addText("\nStage: " + _stageTracker.stage.id);
	
	boolean friendly;
	if(owner != null)
	    friendly = Player.isLocal(owner);
	else if(!ships.isEmpty())
	    friendly = Player.isLocal(ships.iterator().next().getPlayer());
	else
	    return;
	
	if(_contested)
	    text.addText(" [Contested]", ColorRGBA.Yellow);
	else if(_beingReverted)
	    text.addText(" [Reversion in progress]", friendly ? ColorRGBA.Red : ColorRGBA.Green);
	else if(_stageTracker.stage.isLast())
	    text.addText(" [Lockdown]", ColorRGBA.Blue);
	else if(!ships.isEmpty())
	    text.addText(" [Capture in progress]", friendly ? ColorRGBA.Green : ColorRGBA.Red);
    }
    
    private void writeProgress(ColoredText text) {
	if(owner == null && ships.isEmpty())
	    return;
	text.addText("\n");
	if(_beingReverted)
	    text.addText("Reversion progress: " + _stageTracker.revPoints + "/" + _stageTracker.stage.revCost);
	else
	    text.addText("Capture progress: " + _stageTracker.stagePoints + "/" + _stageTracker.stage.advCost);
    }
    
    private void writeFormation(ColoredText text) {
	if(_contested || ships.isEmpty())
	    return;
	int value = ships.size();
	Ship central = anchor.getShip();
	if(central != null) {
	    if(value > 1)
		++value;
	    if(central.getPlayer().hasTech("kprioritization"))
		++value;
	}
	text.addText("\n");
	if(_beingReverted)
	    text.addText("Reversion " );
	else
	    text.addText("Capture ");
	text.addText("rate: " + value + "/turn");
    }
    
    public float getColorMultiplier() {
	return _stageTracker.stage.colorMult;
    }
    
    private static final class StageTracker {
	
	Stage stage;
	int stagePoints, revPoints;
	
	StageTracker(Stage stage) {
	    this.stage = stage;
	}
	
	List<Stage> points(int amount, boolean reversion) {
	    List<Stage> result = new ArrayList<>();
	    if(reversion)
		revPoints += amount;
	    else
		stagePoints += amount;
	    while(stage.next != null && stagePoints >= stage.advCost) {
		stagePoints -= stage.advCost;
		revPoints = 0;
		stage = stage.next;
		result.add(stage);
	    }
	    while(stage.prev != null && revPoints >= stage.revCost) {
		revPoints -= stage.revCost;
		stagePoints = 0;
		stage = stage.prev;
		result.add(stage);
	    }
	    return result;
	}
	
    }
    
    private static final class Stage {
	
	Stage next, prev;
	int advCost, revCost, id;
	float colorMult;
	
	Stage(int advCost, int revCost, float colorMult) {
	    this.advCost = advCost;
	    this.revCost = revCost;
	    this.colorMult = colorMult;
	}
	
	Stage next(int advCost, int revCost, float colorMult) {
	    next = new Stage(advCost, revCost, colorMult);
	    next.prev = this;
	    next.id = id + 1;
	    return next;
	}
	
	Stage first() {
	    Stage s = this;
	    while(s.prev != null)
		s = s.prev;
	    return s;
	}
	
	boolean isLast() {
	    return next == null;
	}
	
    }
    
}
