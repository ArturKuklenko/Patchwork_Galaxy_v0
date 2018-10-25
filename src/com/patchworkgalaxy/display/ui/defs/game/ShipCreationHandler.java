package com.patchworkgalaxy.display.ui.defs.game;

import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.prefab.ModelComponentDescriptor;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;

class ShipCreationHandler implements Subscriber<Ship> {
    
    private final GameUIPD _gameUI;
    private final TileManager _tileManager; //used to update tooltips
    
    ShipCreationHandler(GameUIPD gameUI, TileManager tileManager) {
	_gameUI = gameUI;
	_tileManager = tileManager;
    }

    @Override public void update(Subscribable<? extends Ship> topic, Ship message) {
	UI.Instance.showPanel(new StandardPanelDescriptor(getShipComponentDescriptor(message)));
    }
    
    private ComponentDescriptor getShipComponentDescriptor(final Ship ship) {
	return new ModelComponentDescriptor(ship.getModel())
		.addCallback(new Action() {
		    @Override public void act(Component actOn) {
			_gameUI.updateCommandCard(ship, null);
		    }
		}.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.addCallback(getTileInitAction(ship).asCallback(ComponentCallback.Type.INITIALIZE))
		;
    }
    
    private Action getTileInitAction(final Ship ship) {
	return new Action() {
	    @Override public void act(Component actOn) {
		ship.addSubscription(getTileUpdateSubscriber(ship));
	    }
	};
    }
    
    private Subscriber<Object> getTileUpdateSubscriber(final Ship ship) {
	return new Subscriber<Object>() {
	    @Override public void update(Subscribable<? extends Object> topic, Object message) {
		PatchworkGalaxy.schedule(new Runnable() {
		    @Override public void run() {
			_tileManager.updateTile(ship.getPosition());
		    }
		});
	    }
	};
    }
    
}
