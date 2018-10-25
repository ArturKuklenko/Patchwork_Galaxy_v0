package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipSystemTemplate;

class Utils {

    private Utils() {}
    
    static EvolutionStrategy getEvolutionStrategy(Evolution evolution) throws EvolutionException {
	Opcode opcode = evolution.getOpcode();
	if(opcode == null)
	    return new EvolutionStrategy.Fire();
	switch(opcode) {
	case NO_OP:
	    return new EvolutionStrategy.Noop();
	case SELECT:
	    return new EvolutionStrategy.Select();
	case DESELECT:
	    return new EvolutionStrategy.Deselect();
	case MAKESHIPS:
	    return new EvolutionStrategy.MakeShips();
	case MAKEGROUP:
	    return new EvolutionStrategy.MakeGroup();
	case BUILD:
	    return new EvolutionStrategy.Build();
	case RESEARCH:
	    return new EvolutionStrategy.Research();
	case PATCH:
	    return new EvolutionStrategy.Patch();
	case ENDTURN:
	    return new EvolutionStrategy.EndTurn();
	case DEFEAT:
	    return new EvolutionStrategy.Defeat();
	case CONCEDE:
	    return new EvolutionStrategy.Concede();
	case SPECIAL:
	    return new EvolutionStrategy.Special();
	default:
	    throw new EvolutionException("Unknown evolution opcode " + opcode);
	}
    }
    
    static void verifyPlayer(GameState gameState, Evolution evolution, Player player) throws EvolutionException {
	if(evolution.getPlayerId() != 255 && evolution.getPlayerId() != player.getId())
	    throw new EvolutionException("Player mismatch: " + evolution.getPlayerId() + " vs. " + player.getId());
    }
    
    static Player getPlayer(GameState gameState, Evolution evolution) throws EvolutionException {
	try {
	    return gameState.getPlayer(evolution.getPlayerId());
	}
	catch(IndexOutOfBoundsException e) {
	    throw new EvolutionException("Bad playerId " + evolution.getPlayerId());
	}
    }
    
    static Coordinate getHighCoordinate(GameState gameState, Evolution evolution) throws EvolutionException {
	byte[] bytes = evolution.getArgBytes();
	return new Coordinate(bytes[0], bytes[1]);
    }
    
    static Coordinate getLowCoordinate(GameState gameState, Evolution evolution) throws EvolutionException {
	byte[] bytes = evolution.getArgBytes();
	return new Coordinate(bytes[2], bytes[3]);
    }
    
    static Tile getHighTile(GameState gameState, Evolution evolution) throws EvolutionException {
	byte[] bytes = evolution.getArgBytes();
	Tile tile = gameState.tileAtCoordinates(bytes[0], bytes[1]);
	if(tile == null)
	    throw new EvolutionException("Bad tile (" + bytes[0] + ", " + bytes[1] + ")");
	else
	    return tile;
    }
    
    static Tile getLowTile(GameState gameState, Evolution evolution) throws EvolutionException {
	byte[] bytes = evolution.getArgBytes();
	Tile tile = gameState.tileAtCoordinates(bytes[2], bytes[3]);
	if(tile == null)
	    throw new EvolutionException("Bad tile (" + bytes[2] + ", " + bytes[3] + ")");
	else
	    return tile;
    }
    
    static ShipSystem getHighSystem(GameState gameState, Evolution evolution) throws EvolutionException {
	Ship ship = gameState.getShipById(evolution.getArgShorts()[0]);
	if(ship == null)
	    throw new EvolutionException("Invalid ship id");
	verifyPlayer(gameState, evolution, ship.getPlayer());
	byte systemId = (byte)(evolution.getRawOpcode() & 0x7F);
	ShipSystemTemplate template = TemplateRegistry.SYSTEMS.lookup(systemId);
	return ship.getSystem(template.getName(), true);
    }
    
    static Object getHighKvpEntry(GameState gameState, Evolution evolution, String prefix) throws EvolutionException {
	Object o1 = gameState.lookup(prefix);
	if(!(o1 instanceof TemplateRegistry))
	    throw new EvolutionException("Bad prefix " + prefix);
	Object o2 = ((TemplateRegistry)o1).lookup(evolution.getArgShorts()[0]);
	if(o2 == null)
	    throw new EvolutionException("Couldn't find entry " + evolution.getArgs() + " in " + prefix);
	return o2;
    }
    
}
