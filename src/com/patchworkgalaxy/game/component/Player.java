package com.patchworkgalaxy.game.component;

import com.jme3.math.Vector3f;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.display.appstate.GameAppState;
import com.patchworkgalaxy.game.misc.Faction;
import com.patchworkgalaxy.game.misc.Tech;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.game.state.evolution.EvolutionBuilder;
import com.patchworkgalaxy.game.state.evolution.Opcode;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.game.tile.TileBoard;
import com.patchworkgalaxy.game.vital.Vital;
import com.patchworkgalaxy.game.vital.VitalStore;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player extends GameComponent {
    
    private final GameState _gameState;
    
    private final Subcomponents<Ship> _ships;
    private final List<Integer> _shipsOfTier;
    
    List<ShipDeployment> patchingShipInfo;
    List<ShipDeployment> shipsArrivingThisTurn;
    
    private Ship _headquarters;
    private int _turnCount = 0;
    
    private final Vital _credits, _researchPoints, _researchUpgrade, _income;
    private final VitalStore _vitals;
    
    private int _patchTilesAvailable;
    private TileBoard _patchSpace;
    final Set<Tech> techs;
    
    public final int id;
    
    private final Faction _faction;
    
    public Player(GameProps props, GameState gameState, int id) {
	super(props, Breed.PLAYER);
        this.id = id;
        _ships = new Subcomponents<>();
        patchingShipInfo = new LinkedList<>();
        shipsArrivingThisTurn = new ArrayList<>();
	_gameState = gameState;
	
	_vitals = new VitalStore();
	_credits = _vitals.addVital("credits");
	_researchPoints = _vitals.addVital("research");
	_researchUpgrade = _vitals.addVital("research_upgrade");
	_vitals.addVital("ir_damage");
	_income = _vitals.addVital("income", 20);
	_income.damage(_income.getCurrent());
	
	techs = new HashSet<>();
	_faction = TemplateRegistry.FACTIONS.lookup(props.getString("Faction"));
	_shipsOfTier = new ArrayList<>();
        
    }
    
    public boolean isCurrentPlayer() {
        return this == _gameState.getCurrentPlayer();
    }
    
    public boolean isLocal() {
	return isLocal(this);
    }
    
    public static boolean isLocal(Player player) {
        return GameAppState.isFriendly(player);
    }
    
    @Override public GameState getGameState() {
	return _gameState;
    }
    
    @Override public Faction getFaction() {
	return _faction;
    }
    
    public void addShip(Ship ship) {
        this._ships.add(ship);
        if(_headquarters == null)
            _headquarters = ship;
	int tier = ship.getTier() - 1;
	if(tier >= 0) {
	    while(tier >= _shipsOfTier.size())
		_shipsOfTier.add(0);
	    int i = _shipsOfTier.get(tier);
	    _shipsOfTier.set(tier, i + 1);
	}
    }
    
    public void removeShip(Ship ship) {
        this._ships.remove(ship);
	int tier = ship.getTier() - 1;
	if(tier >= 0 && tier < _shipsOfTier.size()) {
	    int i = _shipsOfTier.get(tier);
	    _shipsOfTier.set(tier, i - 1);
	}
    }
    
    public boolean canBuild(ShipTemplate template) {
	return checkBuild(template) != null;
    }
    
    public void build(ShipTemplate template) {
	ShipDeployment build = checkBuild(template);
	if(build == null)
	    throw new IllegalArgumentException();
        int cost = build.getBuildCost();
        _credits.damage(cost);
	build.createPatchingShip(this, _patchSpace, true);
        patchingShipInfo.add(build);
    }
    
    private ShipDeployment checkBuild(ShipTemplate template) {
	if(!canIssueOrders())
	    return null;
	if(template.isHeroic()) {
	    if(!_ships.getNamedBucket(template.getName()).isEmpty())
		return null;
	}
	ShipDeployment build = new ShipDeployment(template.getProps(), this);
        int cost = build.getBuildCost();
        //return build;
        //adolfnewnewnew
        if(cost > _credits.getCurrent())
            return null;
	return build;
    }
    
    public boolean sendResearchMessage(Tech research) {
	if(!canIssueOrders())
	    return false;
	if(!research.isResearchableBy(this))
	    return false;
	getGameState().safeEvolve(
	    new EvolutionBuilder(Opcode.RESEARCH, (byte)id)
	    .shift((short)TemplateRegistry.TECHS.getId(research.getName()))
	    .shift((short)0)
	    .getEvolution()
	);
	return true;	
    }
    
    public boolean research(Tech research) {
	boolean legal = research.researchedBy(this);
	if(legal && Player.isLocal(this)) {
	    int level = research.getLevelForPlayer(this);
	    String name = research.getNameForLevel(level);
	    ChatMessage logthat = new ChatMessage(" has researched " + name + "!");
	    logthat.suppressColon();
	    ClientManager.client().send(logthat);
	}
	return legal;
    }
    
    public void afterResearch(Tech research) {
	techs.add(research);
    }
    
    public boolean setPatchCoordinates(Tile tile) {
	if(!hasArrivingShips())
	    throw new IllegalStateException("Tried to set patch coordinates, but no ships are slated to arrive");
        if(!tile.isValidPatchTileFor(this))
            return false;
	ShipDeployment arrival = shipsArrivingThisTurn.remove(0);
        getGameState().safeEvolve(
	    new EvolutionBuilder(Opcode.MAKESHIPS, (byte)id)
	    .shift(TemplateRegistry.SHIPS, arrival.getName())
	    .shift(tile)
	    .getEvolution()
	);
	--_patchTilesAvailable;
	return true;
    }
    
    public boolean hasArrivingShips() {
        return _patchTilesAvailable > 0 && !shipsArrivingThisTurn.isEmpty();
    }
    
    /**
     * Checks if this player can issue orders.
     * <p>
     * A player can only issue orders on his or her own turn, and cannot issue
     * orders if he or she still has patch-ins to resolve. The player also has
     * to be alive.
     * </p>
     * @return if this player can issue orders
     */
    public boolean canIssueOrders() {
	
	//dead men issue no orders
	if(!alive())
	    return false;
	
	//patch-ins have to be handled before any other actions
	if(hasArrivingShips())
	    return false;
	
	//no acting on enemy turns!
	if(this != _gameState.getCurrentPlayer())
	    return false;
	
	return true;
	
    }
    
    public boolean owns(GameComponent gameObject) {
	return gameObject.getPlayer() == this;
    }
    
    /**
     * Checks if this player can issue orders to a particular ship.
     * <p>
     * In addition to the {@link Player#canIssueOrders() normal requirements for
     * issuing orders}, the player must control that ship. This does not check
     * if the ship can meaningfully <em>react to</em> orders (eg. the check is
     * true for a ship that's out of TB).
     * </p>
     * @param ship the ship to check
     * @return if this player can issue orders to that ship
     */
    public boolean canIssueOrdersTo(Ship ship) {
	
	//we have to be able to issue orders at all
	if(!canIssueOrders())
	    return false;
	
	//and we have to command the ship in question
	return owns(ship);
	
    }
    
    public ShipDeployment getFirstArrivingShip() {
        if(!hasArrivingShips())
            return null;
        return shipsArrivingThisTurn.get(0);
    }
    
    @Override
    public void turnStart() {
	super.turnStart();
        ++_turnCount;
	updateIncome();
        _credits.regenerate(_income.getCurrent());
	updateRP();
	countPatchTiles();
        //adolfnewnewnew start
        handleHangarAutobuild();
        //adolfnewnewnew end
	handleArrivals();
	getGameState().setNamespace("~player", this);
	for(Ship ship : _ships)
	    ship.turnStart();
    }
    
    @Override
    public void turnEnd() {
	super.turnEnd();
	exitPatchSpace();
	for(Ship ship : _ships){
	    ship.turnEnd();
        }
    }
    
    private void updateIncome() {
	_income.regenerate(1);
    }
    
    private void updateRP() {
	float upgrade = _researchUpgrade.getCurrent();
	float upgradePercent = upgrade / 100;
	float base = 10;
	float gain = base * (upgradePercent + 1);
	_researchPoints.regenerate((int)gain);
    }
    
    private void handleArrivals() {
        Iterator<ShipDeployment> i = patchingShipInfo.iterator();
	int arrivals = 0;
	shipsArrivingThisTurn.removeAll(shipsArrivingThisTurn);
        while(i.hasNext()) {
            ShipDeployment s = i.next();
	    boolean arrival = s.patch(1);
	    if(arrival && (arrivals < _patchTilesAvailable)) {
	        shipsArrivingThisTurn.add(s);
	        i.remove();
	        ++arrivals;
	    }
        }
    }
    
    private void countPatchTiles() {
	if(_headquarters == null) {
	    _patchTilesAvailable = 0;
	    return;
	}
	int x = _headquarters.getPosition().x;
	Set<Tile> patchTiles = new HashSet<>();
	for(int dx = -2; dx < 2; ++dx) {
	    int y = 0;
	    Tile tile;
	    do {
		tile = _gameState.getBoard().tileAtCoordinates(x + dx, y);
		++y;
		if(tile != null && tile.isValidPatchTileFor(this)) {
		    if(!patchTiles.contains(tile)) {
			patchTiles.add(tile);
		    }
		}
	    } while(tile != null);
	}
	_patchTilesAvailable = patchTiles.size();
    }
    
    public Ship getHeadquarters() {
        return _headquarters;
    }
    
    public int getCredits() {
        return _credits.getCurrent();
    }
    
    public int getResearchPoints() {
	return _researchPoints.getCurrent();
    }
    
    public void addCredits(int amount) {
	_credits.regenerate(amount);
    }
    
    public int getTurnCount() {
	return _turnCount;
    }
    
    public int getIncome() {
	return _income.getCurrent();
    }
    
    public int getIncome(int offset) {
	return Math.min(_income.getCurrent() + offset, _income.getMax());
    }
    
    public int getMaxIncome() {
	return _income.getMax();
    }
    
    public void addResearchPoints(int amount) {
	_researchPoints.regenerate(amount);
    }
    
    public void enterPatchSpace() {
	if(_patchSpace == null) {
	    createPatchBoard();
	    PatchworkGalaxy.getBoardCameraControl().lookAt(_patchSpace);
	}
    }
    
    public void exitPatchSpace() {
	if(_patchSpace != null) {
	    writePatchBoard();
	    PatchworkGalaxy.getBoardCameraControl().lookAt(_gameState.getBoard());
	}
    }
    
    private void createPatchBoard() {
	if(_patchSpace != null)
	    _patchSpace.destroy();
	final int naturalWidth = 11;
	final int rowsNeeded = 5;
	ShipDeployment[][] rows = new ShipDeployment[rowsNeeded][patchingShipInfo.size()];
	int[] rowSizes = new int[rowsNeeded];
	int width = naturalWidth;
	for(ShipDeployment sd : patchingShipInfo) {
	    int row = sd.getPatchTime();
	    rows[row][rowSizes[row]] = sd; 
	    ++rowSizes[row];
	    width = Math.max(width, rowSizes[row]);
	}
	TileBoard ps = new TileBoard(_gameState, new Vector3f(0, 800, -1000), true);
	ps.rectangle(width, rowsNeeded);
	for(int y = rowsNeeded; --y >= 0;) {
	    for(int x = rowSizes[y]; --x >= 0;) {
		rows[y][x].createPatchingShip(this, ps, false);
	    }
	}
	_patchSpace = ps;
    }
    
    private void writePatchBoard() {
	for(ShipDeployment sd : patchingShipInfo)
	    sd.writePatchingShip();
	Collections.sort(patchingShipInfo, new Comparator<ShipDeployment>() {
	    @Override
	    public int compare(ShipDeployment o1, ShipDeployment o2) {
		return o1.getPriority() - o2.getPriority();
	    }
	});
	_patchSpace.destroy();
	_patchSpace = null;
    }

    @Override
    public Tile getPosition() {
	return null;
    }

    @Override
    public Object lookup(String name) {
	switch (name) {
	case "hq":
	case "headquarters":
	    return _headquarters;
	case "enemy":
	    return _gameState.getEnemyOf(this);
	case "techs":
	    return new Namespace() {
		@Override
		public Object lookup(String name) {
		    return Player.this.hasTech(name) ? Player.this : null;
		}
	    };
	case "highest_tier":
	    for(int i = _shipsOfTier.size(); --i >= 0;) {
		if(_shipsOfTier.get(i) > 0)
		    return i + 1;
	    }
	    return 0;
	default:
	    Vital vital = _vitals.getVital(name);
	    if(vital != null)
		return vital;
	    else
		return super.lookup(name);
	}
    }
    
    public Set<Ship> getShips() {
	return _ships.getAll();
    }
    
    public boolean hasTech(String name) {
	Tech tech = TemplateRegistry.TECHS.lookup(name);
	return hasTech(tech);
    }
    
    public boolean hasTech(Tech tech) {
	return tech.wasResearchedBy(this);
    }
    
    public boolean specialVictory() {
	return false;
    }
    
    @Override
    Vector3f getDefaultHeading() {
	if(id % 2 == 0)
	    return new Vector3f(1, 0, 0);
	else
	    return new Vector3f(-1, 0, 0);
    }

    @Override
    public Player getPlayer() {
	return this;
    }
    
    public boolean consumeCredits(int amount) {
	
	if(_credits.getCurrent() >= amount) {
	    _credits.damage(amount);
	    return true;
	}
	else
	    return false;
	
    }
    
    public boolean consumeRP(int amount) {
	
	if(_researchPoints.getCurrent() >= amount) {
	    _researchPoints.damage(amount);
	    return true;
	}
	else
	    return false;
	
    }
    
    public byte getId() {
	return (byte)id;
    }
    
    public boolean isObserver() {
	return id < 0;
    }
    
    public void pass() {
	if(!canIssueOrders()) return;
	getGameState().safeEvolve(
		new EvolutionBuilder(Opcode.ENDTURN, (byte)id).getEvolution()
		);
        
    }
    
    public void concede() {
	getGameState().safeEvolve(
		new EvolutionBuilder(Opcode.CONCEDE, (byte)id).getEvolution()
		);
    }

    @Override
    public double getAccuracyFalloff() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //adolfnewnewnewStart
    private void handleHangarAutobuild() {
       for (Ship checkHangarShip : _ships) {
           if (checkHangarShip.getPlayer().getDisplayName().equals(this.getDisplayName()) && checkHangarShip.getHangarCapacity() > 0 && checkHangarShip.getDockedShips().size() < checkHangarShip.getHangarCapacity()) {
               Ship newShip = null;
               newShip = checkHangarShip.getCompatibleTemplate().instantiate(this, checkHangarShip.getPosition());
               //this.addShip(newShip);
               //newShip.setCarriedBy(checkHangarShip);
               newShip.setHangarGraphic(false);
               try {
                   Thread.sleep(100);
               } catch (InterruptedException ex) {
                   Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
               }
               
               newShip.onJoinGame();
               newShip.setHangarGraphic(true);
           }
       } 
    }
    //adolfnewnewnewEnd
}
