package com.patchworkgalaxy.game.state;

import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.game.component.Condition;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.game.state.evolution.EvolutionAcceptor;
import com.patchworkgalaxy.game.state.evolution.EvolutionException;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileBoard;
import com.patchworkgalaxy.game.tile.TileGroup;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.general.subscriptions.BaseSubscribable;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameState implements Namespace {
    
    private final List<Ship> _ships;
    private final EventQueue _eventQueue, _virtualQueue;
    private final Set<Condition> _globalConditions;
    private final TileBoard _board;
    private final Map<String, Namespace> _namespaces;
    private PlayerList _players;
    private final List<TileGroup> _tileGroups;
    
    private GameRNG _gameRng;
    
    private final EvolutionAcceptor _evolve;
    private final GameHistory _history;
    
    private final Subscribable<Ship> _shipCreationTopic = new BaseSubscribable<>();
    private final Subscribable<Tile> _tileCreationTopic = new BaseSubscribable<>();
    private final Subscribable<Player> _turnTopic = new BaseSubscribable<>();
    
    private final boolean _graphical;

    public GameState(GameHistory history, EvolutionAcceptor.Type mode, boolean graphical) {
	_ships = new ArrayList<>();
	_graphical = graphical;
	_eventQueue = new EventQueue(false);
	_virtualQueue = new EventQueue(true);
	_namespaces = new HashMap<>();
	_globalConditions = new HashSet<>();
	_tileGroups = new ArrayList<>();
	setNamespace("ship", TemplateRegistry.SHIPS);
	setNamespace("system", TemplateRegistry.SYSTEMS);
	setNamespace("event", TemplateRegistry.EVENTS);
	setNamespace("weapon", TemplateRegistry.WEAPONS);
	setNamespace("formula", TemplateRegistry.FORMULAE);
	setNamespace("condition", TemplateRegistry.CONDITIONS);
	setNamespace("tech", TemplateRegistry.TECHS);
	setNamespace("faction", TemplateRegistry.FACTIONS);
	_gameRng = new GameRNG();
	_history = new GameHistory(history);
	initPlayers(_history.getPlayers());
	_board = new TileBoard(this);
	_evolve = EvolutionAcceptor.create(this, mode);
	for(Evolution evolution : _history) {
	    try {
		_evolve.accept(evolution, this);
	    }
	    catch(EvolutionException e) {
		throw new RuntimeException(e);
	    }
	}
    }
    
    private void initPlayers(GameProps... playerdefs) {
	if(_players != null)
	    throw new IllegalStateException("Can't reinitialize players");
	int startingPlayer = _gameRng.getInt(playerdefs.length);
	_players = new PlayerList(this, startingPlayer, playerdefs);
	doTurnStart(getCurrentPlayer());
	if(playerdefs.length == 2)
	    _players.getNextPlayer().addCredits(1);
    }
    
    public void safeEvolve(Evolution evolution) {
	try {
	    evolve(evolution);
	}
	catch(EvolutionException e) {}
    }
    
    public void evolve(Evolution evolution) throws EvolutionException {
	if(evolution != null)
	    _evolve.accept(evolution, this);
	_eventQueue.processEvents();
	_players.update();
	Topic.GAME_CHRONO.update();
    }
    
    public void enqueueEvent(GameEvent event) {
	(event.isVirtual() ? _virtualQueue : _eventQueue).enqueue(event);
    }
    
    public double roll(Object requestor) {
	return _gameRng.roll(requestor);
    }
    
    public void setRngSeed(long seed) {
	_gameRng.setSeed(seed);
    }
    
    public Player getCurrentPlayer() {
        return _players.getCurrentPlayer();
    }
    
    public Player getEnemyOf(Player player) {
	return _players.getEnemyOf(player);
    }
    
    public List<Player> getPlayers() {
	return Arrays.asList(_players.getRawPlayers());
    }
    
    public Player getPlayer(int id) {
        return _players.getPlayer(id);
    }
    
    public int countPlayers() {
	return _players.size();
    }
    
    public Player getWinner() {
	return _players.getWinner();
    }
    
    public void advanceTurnCount() {
	Player current = getCurrentPlayer();
	if(!current.canIssueOrders()) return;
	doTurnEnd(current);
	_players.advanceCurrentPlayer();
	doTurnStart(getCurrentPlayer());
    }
    //adolf
    private void doTurnStart(Player player) {
	player.turnStart();
        try {
                   Thread.sleep(100);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
               }
	for(TileGroup tg : _tileGroups)
	    tg.turnStart();
	_eventQueue.processEvents();
	_turnTopic.update(player);
    }
    
    private void doTurnEnd(Player player) {
	player.turnEnd();
        try {
                   Thread.sleep(100);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
               }
	for(TileGroup tg : _tileGroups)
	    tg.turnEnd();
	_eventQueue.processEvents();
    }
    //adolfend
    public Tile tileAtCoordinates(int x, int y) {
        return _board.tileAtCoordinates(x, y);
    }
    
    public TileBoard getBoard() {
        return _board;
    }
    
    public final void setNamespace(String key, Namespace namespace) {
	_namespaces.put(key, namespace);
    }
    
    public final Namespace getNamespace(String key) {
	return _namespaces.get(key);
    }

    @Override
    public Object lookup(String name) {
	try {
	    String[] path = name.split(":");
	    Object o = new Namespace() {
		@Override
		public Object lookup(String name) {
		    return _namespaces.get(name);
		}
	    };
	    for(String step : path) {
		if(!(o instanceof Namespace))
		    return null;
		o = ((Namespace)o).lookup(step);
	    }
	    return o;
	}
	catch(NullPointerException e) {
	    String message = "NPE in GameState.lookup(" + name + ")";
	    NullPointerException e2 = new NullPointerException(message);
	    e2.setStackTrace(e.getStackTrace());
	    PatchworkGalaxy.writeException(e2);
	    return null;
	}
    }
    
    public void addGlobalCondition(Condition condition) {
	if(!condition.isGlobal())
	    throw new IllegalArgumentException("Tried to add a non-global condition to a game state");
	_globalConditions.add(condition);
    }
    
    public Set<Condition> getGlobalConditions() {
	Iterator<Condition> i = _globalConditions.iterator();
	while(i.hasNext()) {
	    if(!i.next().alive())
		i.remove();
	}
	return new HashSet<>(_globalConditions);
    }
    
    void createTileGroup(GameProps groupProps, Tile anchor, int radius) {
	_tileGroups.add(_board.createTileGroup(groupProps, anchor, radius));
    }
    
    public boolean isGraphical() {
	return _graphical;
    }
    
    EvolutionAcceptor getEvolutionAcceptor() {
	return _evolve;
    }
    
    public Random getRandom() {
	return _gameRng.asRandom();
    }
    
    public void onComponentAdded(GameComponent component) {
	if(component.getClass() == Ship.class) {
	    synchronized(this) { 
		Ship ship = (Ship)component;
		_shipCreationTopic.update(ship);
		ship.register((short)_ships.size());
		_ships.add(ship);
	    }
	}
	if(component.getClass() == Tile.class)
	    _tileCreationTopic.update((Tile)component);
    }
    
    public Ship getShipById(short id) {
	try {
	    return _ships.get(id);
	} catch(IndexOutOfBoundsException e) {
	    return null;
	}
    }
    
    public void addShipCreationListener(Subscriber<? super Ship> listener) {
	_shipCreationTopic.addSubscription(listener);
    }
    
    public void addTileCreationListener(Subscriber<? super Tile> listener) {
	_tileCreationTopic.addSubscription(listener);
    }
    
    public void addTurnListener(Subscriber<? super Player> listener) {
	_turnTopic.addSubscription(listener);
    }
    
}
