package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.subscriptions.Topic;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameUIPD extends StandardPanelDescriptor {
    
    private final CommandCardController _ccc;
    private TileManager _tiles;
    private final LocationViewerCD _locationViewer;
    private final GameState _game;
    
    public GameUIPD(GameState game) {
	_game = game;
	_ccc = new CommandCardController(this);
	List<ComponentDescriptor> components = new ArrayList<>();
	components.add(getCleanupComponent());
	if(!GameAppState.getLocalPlayer().isObserver())
	    components.add(new ResourcesViewer(_game));
	components.addAll(getGameMenu());
	_locationViewer = new LocationViewerCD();
	components.add(_locationViewer);
	setComponents(components);
    }
    
    @SuppressWarnings("unchecked")
    @Override public void onShow(Panel panel) {
	_tiles = TileManager.create(this, _game);
	_game.addShipCreationListener(new ShipCreationHandler(this, _tiles));
	_game.addTileCreationListener(_tiles);
    }
    
    void updateCommandCard() {
	_ccc.update();
    }
    
    void updateCommandCard(Ship ship, ShipSystem system) {
	if(system != null && !system.getPlayer().isLocal()) return;
	ShipSystem oldSystem = _ccc.getSystem();
	Ship oldShip = _ccc.getShip();
	Set<Tile> tiles = new HashSet<>();
	if(oldSystem != null)
	    tiles.addAll(oldSystem.getLegalTargets());
	if(system != null)
	    tiles.addAll(system.getLegalTargets());
	if(ship != null)
	    tiles.add(ship.getPosition());
	if(oldShip != null)
	    tiles.add(oldShip.getPosition());
	_ccc.update(ship, system);
	for(Tile tile : tiles)
	    _tiles.updateTile(tile);
    }
    
    void updateLocation(Tile tile) {
	_locationViewer.update(tile.getStrategicGroup());
    }
    
    Ship getShip() {
	return _ccc.getShip();
    }
    
    ShipSystem getSystem() {
	return _ccc.getSystem();
    }
    
    private ComponentDescriptor getCleanupComponent() {
	return new StandardComponentDescriptor("Clean Up Command Card", Vector2f.ZERO, Vector2f.ZERO)
		.addCallback(new Action() {
		    @Override public void act(Component actOn) {
			UI.Instance.showPanelWithTag(null, "Command Card");
			actOn.getPanel().hide();
		    }
		}.asCallback(ComponentCallback.Type.UPDATE))
		.addSubscription(Topic.GAME_ENDED)
		;
    }
    
    private static List<ComponentDescriptor> getGameMenu() {
	List<ComponentDescriptor> result = new ArrayList<>();
	for(int i = 0; i < Definitions.GAME_MENU_BUTTON_LABELS.length; ++i)
	    result.add(new GameMenuButtonDescriptor(i));
	result.add(new GameMenuTriggerDescriptor());
	result.add(getConfirmConcedeButton());
	return result;
    }
    
    private static ComponentDescriptor getConfirmConcedeButton() {
	return new StandardComponentDescriptor("Confirm Concede",
		GameMenuTriggerDescriptor.getButtonOrigin(0, 1),
		Definitions.GAME_MENU_BUTTON_SIZE)
		.setOpacity(0f)
		.setText(new ColoredText("Confirm"))
		.setTextAlignCenter()
		.setTextSize(16)
		.setBackgroundImage(Definitions.GAME_MENU_BUTTON_RED_IMAGE)
		.setZIndex(Definitions.Z_INDEX_HIGH - 50)
		.addTransition(1f, Property.OPACITY, Property.CENTER)
		.addCallback(new Action() {
		    @Override public void act(Component actOn) {
			GameMenuTriggerDescriptor.hideGameMenuButtons(actOn);
			GameAppState.getLocalPlayer().concede();
		    }
		}.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		;
    }
    
}
