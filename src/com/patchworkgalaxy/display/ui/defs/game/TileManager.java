package com.patchworkgalaxy.display.ui.defs.game;

import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardTooltipDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.prefab.ModelComponentDescriptor;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import java.util.Map;
import java.util.WeakHashMap;

class TileManager implements Subscriber<Tile> {
    
    private final GameUIPD _gameUI;
    private final Subscriber<Object> _turn;
    private final Map<Tile, Component> _tiles;
    
    private TileManager(GameUIPD gameUI) {
	_gameUI = gameUI;
	_tiles = new WeakHashMap<>();
	_turn = new Subscriber<Object>() {
	    @Override public void update(Subscribable<? extends Object> topic, Object message) {
		for(Tile tile : _tiles.keySet()) {
		    TileColors.update(tile, false, false);
		}
	    }
	};
    }

    static TileManager create(GameUIPD gameUI, GameState game) {
	TileManager result = new TileManager(gameUI);
	game.addTurnListener(result._turn);
	game.addShipCreationListener(result._turn);
	return result;
    }
    
    @Override public void update(Subscribable<? extends Tile> topic, Tile message) {
	if(_tiles.containsKey(message)) return;
	TileColors.initialize(message);
	UI.Instance.showPanel(new StandardPanelDescriptor(getTileComponentDescriptor(message)));
    }
    
    private ComponentDescriptor getTileComponentDescriptor(final Tile tile) {
	return new ModelComponentDescriptor(tile.getModel())
		.setTooltipDescriptor(new StandardTooltipDescriptor("").setWidth(200f))
		.addCallback(getClickAction(tile).asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.addCallback(getTileInitAction(tile).asCallback(ComponentCallback.Type.INITIALIZE))
		;
    }
    
    private Action getClickAction(final Tile tile) {
	return new Action() {
	    @Override public void act(Component actOn) {
		ShipSystem system = _gameUI.getSystem();
		if(getLocalPlayer().hasArrivingShips())
		    getLocalPlayer().setPatchCoordinates(tile);
		else if(system != null) {
		    _gameUI.getSystem().aim(tile);
		    PatchworkGalaxy.schedule(refreshCommandCard(system.getShip()));
		}
		else {
		    _gameUI.updateCommandCard(tile.getShip(), null);
		    _gameUI.updateLocation(tile);
		}
	    }
	};
    }
    
    private Action getTileInitAction(final Tile tile) {
	return new Action() {
	    @Override public void act(Component actOn) {
		_tiles.put(tile, actOn);
		tile.addSubscription(getTileUpdateSubscriber(tile));
		PatchworkGalaxy.schedule(new Runnable() {
		    @Override public void run() {
			updateTile(tile);
		    }
		});
	    }
	};
    }
    
    private Subscriber<Object> getTileUpdateSubscriber(final Tile tile) {
	return new Subscriber<Object>() {
	    @Override public void update(Subscribable<? extends Object> topic, Object message) {
		PatchworkGalaxy.schedule(new Runnable() {
		    @Override public void run() {
			updateTile(tile);
		    }
		});
	    }
	};
    }
    
    void updateTile(Tile tile) {
	if(tile == null) return; //TODO: eager NPE defense, remove this later
	ShipSystem system = _gameUI.getSystem();
	Component component = _tiles.get(tile);
	boolean highlight = system != null && system.canTarget(tile);
	boolean selected = tile.getShip() == _gameUI.getShip();
	TileColors.update(tile, selected, highlight);
	if(component != null)
	    TileTooltips.update(tile, component, system);
    }
    
    private Runnable refreshCommandCard(final Ship ship) {
	return new Runnable() {
	    @Override public void run() {
		_gameUI.updateCommandCard(ship, null);
	    }
	};
    }
    
    private Player getLocalPlayer() {
	return GameAppState.getLocalPlayer();
    }
    
}
