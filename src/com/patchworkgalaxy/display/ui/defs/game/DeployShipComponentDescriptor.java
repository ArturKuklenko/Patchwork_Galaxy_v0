package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.display.models.Positional;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardTooltipDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.prefab.ModelComponentDescriptor;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.state.evolution.EvolutionBuilder;
import com.patchworkgalaxy.game.state.evolution.Opcode;
import com.patchworkgalaxy.general.data.Resolver;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipTemplate;

class DeployShipComponentDescriptor extends ModelComponentDescriptor {
    
    private final ShipTemplate _shipTemplate;
    
    DeployShipComponentDescriptor(ShipTemplate st, int xIndex, int yIndex) {
	super(st.getModelTemplate());
	_shipTemplate = st;
	Vector3f position = new Vector3f(Definitions.PATCH_SPACE_ORIGIN);
	position.x += Definitions.PATCH_SPACE_STEP.x * xIndex;
	position.y += Definitions.PATCH_SPACE_STEP.y * yIndex;
	getModel().setPosition(position);
	getModel().setTarget(new Positional.FromVector(position.addLocal(Definitions.PATCH_SPACE_BUTTON_FACING)));
	addCallback(new BuildShipAction().asCallback(ComponentCallback.Type.MOUSE_CLICK));
	setTooltipDescriptor(new StandardTooltipDescriptor(describeShipForPlayer(st, GameAppState.getLocalPlayer())).setWidth(400f));
	setZIndex(Definitions.Z_INDEX_MED);
    }
    
    private class BuildShipAction extends Action {
	@Override public void act(Component actOn) {
	    Player localPlayer = GameAppState.getLocalPlayer();
	    if(localPlayer.canBuild(_shipTemplate)) {
		localPlayer.getGameState().safeEvolve(
		    new EvolutionBuilder(Opcode.BUILD, localPlayer.getId())
		    .shift(TemplateRegistry.SHIPS, _shipTemplate.getName())
		    .shift((short)0)
		    .getEvolution());
	    }
	}
    }
    
    private static ColoredText describeShipForPlayer(ShipTemplate ship, Player player) {
	ColoredText text = new ColoredText();
	text.addText(ship.getDisplayName());
	text.addText(" [Tier " + ship.getInt("Tier") + "]", ColorRGBA.Blue);
	text.addText(ship.getBoolean("TypeA!") ? " [Type A]" : " [Type B]");
	text.addText("\nHull: " + ship.getInt("Hull"), ColorRGBA.Green);
	text.addText(" Shields: " + ship.getInt("Shield"), ColorRGBA.Cyan);
	text.addText(" TB: " + ship.getInt("Thermal"), ColorRGBA.Orange);
	if(ship.getInt("Shield") > 0)
	    text.addText("\nShield regeneration: " + ship.getInt("ShieldRegen"), ColorRGBA.Cyan);
	text.addText("\n\nCost: " + ship.getInt("BuildCost"), ColorRGBA.Yellow);
	Resolver ptr = new Resolver(player.getGameState(), ship.getString("PatchTime"), true);
	int patchTime = (int)ptr.toFloat(player.getGameState());
	if(patchTime == 0)
	    patchTime = 3; 
	text.addText("\nPatch time: " + patchTime, ColorRGBA.Yellow);
	text.addText("\n\nWeapons:" + describeArmaments(ship) + "\n\n" + ship.getDescription());
	return text;
    }
    
    private static String describeArmaments(ShipTemplate ship) {
	String[] systems = ship.getString("System").split(",");
	if(systems.length == 0)
	    return "";
	StringBuilder sb = new StringBuilder();
	int counter = 0;
	String current, next;
	for(int i = 0; i < systems.length; ++i) {
	    current = systems[i];
	    if(current.equalsIgnoreCase("engine"))
		continue;
	    if(i + 1 < systems.length)
		next = systems[i + 1];
	    else
		next = null;
	    ++counter;
	    if(!current.equals(next)) {
		String localized = Localizer.getLocalizedString(new String[] { "system." + current}, "name");
		sb.append("\n\t").append(counter).append ("x ").append(localized);
		counter = 0;
	    }
	}
	return sb.toString();
    }
    
}
