package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.subscriptions.Topic;


class ResourcesViewer extends StandardComponentDescriptor {
    
    private int _showing;
    private final GameState _game;
    
    ResourcesViewer(GameState game) {
	super(
		    "Resources",
		    new Vector2f(Definitions.RESOURCES_PANEL_X, Definitions.RESOURCES_PANEL_Y),
		    new Vector2f(Definitions.RESOURCES_PANEL_WIDTH, Definitions.RESOURCES_PANEL_HEIGHT)
		);
	_game = game;
	int playerId = GameAppState.getLocalPlayer().id;
	this
	    .setText(describeResources(playerId))
	    .addCallback(new Action() {
		@Override
		public void act(Component actOn) {
		    showCurrent(actOn);
		}
	    }.asCallback(ComponentCallback.Type.UPDATE))
	    .addCallback(new Action() {
		@Override
		public void act(Component component) {
		    showNext(component);
		}
	    }.asCallback(ComponentCallback.Type.MOUSE_CLICK))
	    .addSubscription(Topic.GAME_CHRONO);
	_showing = playerId;
    }
    
    private void showNext(Component component) {
	int max = _game.countPlayers();
	_showing = (_showing + 1) % max;
	component.write(describeResources(_showing), Property.TEXT);
    }
    
    private void showCurrent(Component component) {
	component.write(describeResources(_showing), Property.TEXT);
    }
    
    private ColoredText describeResources(int id) {
	try {
	    Player player = _game.getPlayer(id);
	    return describeResources(player);
	} catch(NullPointerException e) {
	    return new ColoredText();
	}
    }
    
    private ColoredText describeResources(Player player) {
	if(player == null) return new ColoredText();
	boolean allied = player == GameAppState.getLocalPlayer();
	ColoredText text = new ColoredText();
	if(allied) {
	    text.addText("Turn " + Math.max(1, player.getTurnCount()));
		if(player.isCurrentPlayer())
		    text.addText(" [Allied]", ColorRGBA.Green);
		else
		    text.addText(" [Enemy]", ColorRGBA.Red);
	}
	else
	    text.addText("Viewing enemy resources: " + player.getName(), ColorRGBA.Yellow);
	text.addText("\nCredits: " + (allied ? player.getCredits() : "???"));
	text.addText("\nIncome: " + player.getIncome(1) + "/" + player.getMaxIncome());
	text.addText("\nResearch Points: " + player.getResearchPoints());
	Ship hq = player.getHeadquarters();
	if(hq != null)
	    text.addText("\nHQ hull: " + hq.getHullIntegrity() + "/" + hq.getMaxHullIntegrity());
	return text;
    }
    
}
