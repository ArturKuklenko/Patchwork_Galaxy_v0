package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.misc.Tech;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileBoard;
import com.patchworkgalaxy.template.types.ShipTemplate;
import java.util.Set;

interface EvolutionStrategy {
    void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException;
    
    /**
     * Marker interface indicating that the map must be updated after this.
     */
    interface UpdatesMap {}
    
    class Fire implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    ShipSystem system = Utils.getHighSystem(acceptor.getGameState(), evolution);
	    Tile target = Utils.getLowTile(acceptor.getGameState(), evolution);
	    try {
		system.fire(target.asTarget());
	    }
	    catch(IllegalStateException e) {
		throw new EvolutionException("Invalid target for " + system.getName());
	    }
	}
    }
    
    class Noop implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {}
    }
    
    class Defeat implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Utils.getPlayer(acceptor.getGameState(), evolution).kill();
	}
    }
    
    class Build implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Player player = Utils.getPlayer(acceptor.getGameState(), evolution);
	    ShipTemplate shipTemplate = (ShipTemplate)Utils.getHighKvpEntry(acceptor.getGameState(), evolution, "ship");
	    boolean legal = player.canBuild(shipTemplate);
	    if(legal)
		player.build(shipTemplate);
	    else
		throw new EvolutionException("Invalid build (better message TBA)");
	}
    }
    
    class Research implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Tech tech = (Tech)Utils.getHighKvpEntry(acceptor.getGameState(), evolution, "tech");
	    boolean legal = Utils.getPlayer(acceptor.getGameState(), evolution).research(tech);
	    if(!legal)
		throw new EvolutionException("Invalid research (better message TBA)");
	}
    }
    
    class Patch implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Tile tile = Utils.getLowTile(acceptor.getGameState(), evolution);
	    Player player = Utils.getPlayer(acceptor.getGameState(), evolution);
	    boolean legal = player.setPatchCoordinates(tile);
	    if(!legal)
		throw new EvolutionException("Invalid patch-in (better message TBA)");
	}
    }
    
    class EndTurn implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    acceptor.getGameState().advanceTurnCount();
	}
	
    }
    
    abstract class MakeComponent implements EvolutionStrategy {
	private MakeComponent() {}
	abstract void evolveImpl(EvolutionAcceptor acceptor, Evolution evolution, Set<Tile> tiles, Tile extra) throws EvolutionException;
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Set<Tile> tiles = acceptor.getSelectedTiles();
	    Tile extra;
	    if(evolution.getArgShorts()[0] >= 0) {
		extra = Utils.getLowTile(acceptor.getGameState(), evolution);
		if(extra == null)
		    throw new EvolutionException("Attempted to use a nonexistent tile as an extra tile");
		else
		    tiles.add(extra);
	    }
	    else
		extra = null;
	    evolveImpl(acceptor, evolution, tiles, extra);
	    acceptor.setSelectedCoordinates(null);
	}
    }
    
    class MakeShips extends MakeComponent {
	@Override
	void evolveImpl(EvolutionAcceptor acceptor, Evolution evolution, Set<Tile> tiles, Tile extra) throws EvolutionException {
	    ShipTemplate shipTemplate = (ShipTemplate)Utils.getHighKvpEntry(acceptor.getGameState(), evolution, "ship");
	    Player player = Utils.getPlayer(acceptor.getGameState(), evolution);
	    for(Tile tile : tiles) {
		Ship ship = shipTemplate.instantiate(player, tile);
		ship.onJoinGame();
	    }
	}
    }
    
    class MakeGroup extends MakeComponent {
	@Override
	void evolveImpl(EvolutionAcceptor acceptor, Evolution evolution, Set<Tile> tiles, Tile extra) throws EvolutionException {
	    throw new UnsupportedOperationException();
	}
    }
    
    class MakeTiles implements EvolutionStrategy, UpdatesMap {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    TileBoard board = acceptor.getGameState().getBoard();
	    for(Coordinate coordinate : acceptor.getSelectedCoordinates()) {
		if(board.tileAtCoordinates(coordinate.x, coordinate.x) == null)
		    board.makeTile(coordinate.x, coordinate.y);
	    }
	    acceptor.setSelectedCoordinates(null);
	}
    }
    
    class Select implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Coordinate coordinate = Utils.getHighCoordinate(acceptor.getGameState(), evolution);
	    Set<Coordinate> selected = acceptor.getSelectedCoordinates();
	    selected.add(coordinate);
	    short len = evolution.getArgShorts()[1];
	    while(--len >= 0) {
		coordinate = coordinate.add(1, 0);
		selected.add(coordinate);
	    }
	    acceptor.setSelectedCoordinates(selected);
	}
    }
    
    class Deselect implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    acceptor.setSelectedCoordinates(null);
	}
    }
    
    class Concede implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    Ship hq = Utils.getPlayer(acceptor.getGameState(), evolution).getHeadquarters();
	    hq.kill();
	    hq.updateAppearance();
	}
    }
    
    class Special implements EvolutionStrategy {
	@Override
	public void evolve(EvolutionAcceptor acceptor, Evolution evolution) throws EvolutionException {
	    GameState game = acceptor.getGameState();
	    TileBoard board = game.getBoard();
	    board.testmap();
	    Player p1 = game.getPlayer(0);
	    Player p2 = game.getPlayer(1);
	    Tile p1spawn = board.tileAtCoordinates(0, 5);
	    Tile p2spawn = board.tileAtCoordinates(12, 5);
	    if(p1spawn.getShip() == null)
		p1.getFaction().getHQTemplate().instantiate(p1, p1spawn).onJoinGame();
	    if(p2spawn.getShip() == null)
		p2.getFaction().getHQTemplate().instantiate(p2, p2spawn).onJoinGame();
	}
    }
    
}
