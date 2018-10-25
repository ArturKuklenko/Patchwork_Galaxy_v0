package com.patchworkgalaxy.game.component;

import com.patchworkgalaxy.display.models.AnimationDef;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Resolver;
import com.patchworkgalaxy.template.parser.CanBeEvent;

public class Weapon extends GameComponent implements RenderableComponent {
    
    private final GameComponent _launchedFrom, _target;
    private boolean _missed;
    
    private final Resolver _impactEvent;
    private final boolean _localImpact;
    
    public Weapon(GameProps props, GameComponent launcher, GameComponent target, boolean missed) {
	super(props, Breed.WEAPON);
	_launchedFrom = launcher;
	_target = target;
	_missed = missed;
	_impactEvent = new Resolver(getGameState(), props.getString("Impact"), true);
	_localImpact = props.getBoolean("LocalImpact!");
    }
    
    public String getShortName() {
	return props.getString("ShortName");
    }
    
    public GameComponent getLauncher() {
	return _launchedFrom;
    }
    
    public GameComponent getTarget() {
	return _target;
    }
    
    @Override
    public Tile getPosition() {
	return _launchedFrom.getPosition();
    }
    
    public void impact(int magnitude) {
	GameEvent impact = _impactEvent.resolve(CanBeEvent.class).toEvent(_localImpact ? this : _launchedFrom, _target);
	impact.modify(magnitude, 0);
	impact.enqueue();
    }
    
    @Override
    public void onJoinGame() {
	super.onJoinGame();
	AnimationDef launch = AnimationDef.forWeapon(this);
	_launchedFrom.getModel().play(launch);
    }
    
    public boolean getMissed() {
	return _missed;
    }

    @Override
    public Player getPlayer() {
	return _launchedFrom.getPlayer();
    }

    @Override
    public double getAccuracyFalloff() {

        return _launchedFrom.getAccuracyFalloff();
    }
    
}
