package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.models.ModelSubscriptionType;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.game.commandcard.CommandCard;
import com.patchworkgalaxy.game.commandcard.CommandCardEntry;
import com.patchworkgalaxy.game.commandcard.ThermalBlockType;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import com.patchworkgalaxy.general.subscriptions.Topic;
import java.util.HashSet;
import java.util.Set;

class CCPanelDescriptor extends StandardPanelDescriptor implements Subscriber<Object> {
    
    private final Ship _ship;
    private final CommandCard _commandCard;
    private final CCButtonPlacer _placer;
    private final GameUIPD _gameUI;
    
    CCPanelDescriptor(GameUIPD gameUI, Vector2f center, Ship ship) {
	_gameUI = gameUI;
	_ship = ship;
	_commandCard = new CommandCard(ship);
	_placer = new CCButtonPlacer(_commandCard);
	setCenter(center);
    }
    
    private void setCenter(Vector2f center) {
	Set<ComponentDescriptor> descriptors = new HashSet<>();
	int cb = 0, tb = 0;
	Vector2f where = new Vector2f();
	for(CommandCardEntry entry : _commandCard.getCommandCardEntries()) {
	    where.set(_placer.getButtonOffset(cb)).addLocal(center);
	    descriptors.add(new CCButton(_gameUI, entry, where));
	    ++cb;
	}
	for(ThermalBlockType thermalBlock : _commandCard.getThermalBlocks()) {
	    where.set(_placer.getTBoffset(tb)).addLocal(center);
	    descriptors.add(new CCThermalBlock(thermalBlock, where));
	    ++tb;
	    if(tb >= 6) break;
	}
	int widthMultiplier = Definitions.COMMAND_CARD_BUTTONS_PER_ROW;
	where.set(_placer.getRowPosition(0)).addLocal(center);
	descriptors.add(new CCHealthBar(_commandCard, where, widthMultiplier, false));
	if(_commandCard.getMaxShield() > 0) {
	    where.set(_placer.getRowPosition(1)).addLocal(center);
	    descriptors.add(new CCHealthBar(_commandCard, where, widthMultiplier, true));
	}
	descriptors.add(getDisplayName(center));
	descriptors.add(getBackground(center));
	descriptors.addAll(DockedShipComponentDescriptor.getHangarButtons(_gameUI, _ship));
	setComponents(descriptors);
    }
    
    @Override public void onShow(Panel panel) {
	for(Component component : panel.getComponents()) {
	    component.write(.75f, Property.OPACITY);
	    component.update(Topic.GAME_CHRONO, null);
	}
	_ship.getModel().addSubscription(ModelSubscriptionType.MOTION, this); 
    }
    
    private ComponentDescriptor getDisplayName(Vector2f center) {
	ColoredText text = new ColoredText().addText(
		_commandCard.getDisplayName(),
		_commandCard.isFriendly() ? ColorRGBA.Green : ColorRGBA.Red
		);
	float width = CCButton.FULL_WIDTH * Definitions.COMMAND_CARD_BUTTONS_PER_ROW * 2;
	Vector2f where = _placer.getNamePosition().addLocal(center);
	return new CCComponentDescriptor(
		    null,
		    where,
		    new Vector2f(width, CCThermalBlock.HEIGHT))
		.setText(text)
		.setTextAlignCenter()
		.setZIndex(Definitions.Z_INDEX_HIGH + 60)
		;
    }
    
    private ComponentDescriptor getBackground(Vector2f center) {
	return new CCComponentDescriptor(
		    null,
		    _placer.getCenter().addLocal(center),
		    _placer.getSize())
		.setBackgroundImage(Definitions.COMMAND_CARD_BACKGROUND_IMAGE)
		.setZIndex(Definitions.Z_INDEX_HIGH)
		;
    }

    @Override public void update(Subscribable<? extends Object> topic, Object message) {
	_gameUI.updateCommandCard();
    }
    
}