package com.patchworkgalaxy.game.misc;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.lang.Localizer;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.parser.CanBeEvent;
import java.util.HashMap;
import java.util.Map;

public class Tech {
    
    private final String _name;
    
    private final int _cost;
    private final String _globalEventName, _shipEventName;
    private final boolean _applyShipEventRetroactively;
    private final String[] _prereqs, _prereqNames;
    
    private final Vector2f _center, _dimensions;
    private final String _imagePath;
    
    private final int _maxLevel;
    private final Map<Player, Integer> _levelPerPlayer;
    
    public Tech(GameProps props) {
	_name = props.getString("Name");
	String prereq = props.getString("Prereq");
	_prereqs = prereq == null ? new String[0] : prereq.split(",");
	_prereqNames = new String[_prereqs.length];
	for(int i = _prereqs.length; --i >= 0;)
	    _prereqNames[i] = Localizer.getLocalizedString("tech." + _prereqs[i], "name");
	_cost = props.getInt("Cost");
	
	int tempMaxLevel = props.getInt("Levels");
	if(tempMaxLevel < 1)
	    tempMaxLevel = 1;
	_maxLevel = tempMaxLevel;
	_levelPerPlayer = new HashMap<>();
	
	_applyShipEventRetroactively = !props.getBoolean("Etb!");
	_imagePath = props.getString("Icon");
	
	_globalEventName = props.getString("Initial");
	_shipEventName = props.getString("PerShip");
	
	String[] temp = props.getString("ButtonPos").split(",");
	float[] buttonPos = new float[4];
	for(int i = 0; i < 4; ++i) 
	    buttonPos[i] = Float.valueOf(temp[i]);
	_center = new Vector2f(buttonPos[0], buttonPos[1]);
	_dimensions = new Vector2f(buttonPos[2], buttonPos[3]);
    }
    
    public int getLevelForPlayer(Player player) {
	if(!_levelPerPlayer.containsKey(player))
	    return 0;
	else
	    return _levelPerPlayer.get(player);
    }
    
    public boolean hasLevels() {
	return _maxLevel > 1;
    }
    
    public int getMaxLevel() {
	return _maxLevel;
    }
    
    public boolean wasResearchedBy(Player player) {
	return getLevelForPlayer(player) > 0;
    }
    
    public boolean isResearchableBy(Player player) {
	if(!playerCanAfford(player))
	    return false;
	if(getLevelForPlayer(player) >= _maxLevel)
	    return false;
	if(_prereqs.length == 0)
	    return true;
	return playerMeetsPrereqs(player);
    }
    
    public int getCostForPlayer(Player player) {
	int multiplier = getLevelForPlayer(player) + 1;
	return _cost * multiplier;
    }
    
    public boolean playerCanAfford(Player player) {
	return getCostForPlayer(player) <= player.getResearchPoints();
    }
    
    public boolean playerMeetsPrereqs(Player player) {
	for(String prereqName : _prereqs) {
	    Tech prereq = TemplateRegistry.TECHS.lookup(prereqName);
	    if(prereq.wasResearchedBy(player))
		return true;
	}
	return false;
    }
    
    public String describePrereqs() {
	if(_prereqNames.length == 0)
	    return "";
	else if(_prereqNames.length == 1)
	    return "Prerequisite: " + _prereqNames[0];
	else {
	    String result = "Prerequisite: " + _prereqNames[0];
	    for(int i = 1; i < _prereqNames.length; ++i)
		result += " OR " + _prereqNames[i];
	    return result;
	}
    }
    
    public boolean researchedBy(Player player) {
	
	if(!isResearchableBy(player))
	    return false;
	
	player.consumeRP(getCostForPlayer(player));
	int level = getLevelForPlayer(player) + 1;
	_levelPerPlayer.put(player, level);
	initialApplication(player);
	player.afterResearch(this);
	return true;
	
    }
    
    
    
    private CanBeEvent findEvent(String path, GameState gs, int forLevel) {
	return findEvent(path.replace("%", String.valueOf(forLevel)), gs);
    }
    
    private CanBeEvent findEvent(String path, GameState gs) {
	Object o = gs.lookup(path);
	return (CanBeEvent)o;
    }
    
    private void apply(GameComponent to, String path, boolean allLevels) {
	
	Player player = to.getPlayer();
	int level = getLevelForPlayer(player);
	
	if(allLevels) {
	    for(int i = 1; i < level; ++i) {
		applyLevel(to, path, i);
	    }
	}
	
	applyLevel(to, path, level);
    }
    
    private void applyLevel(GameComponent to, String path, int level) {
	Player player = to.getPlayer();
	CanBeEvent event = findEvent(path, to.getGameState(), level);
	event.toEvent(player, to).enqueue();
    }
    
    void initialApplication(Player player) {
	
	if(_globalEventName != null)
	    apply(player, _globalEventName, false);
	
	if(_applyShipEventRetroactively)
	    initialShips(player);
	
    }
    
    private void initialShips(Player player) {
	if(_shipEventName != null) {
	    for(Ship ship : player.getShips())
		apply(ship, _shipEventName, false);
	}
    }
    
    public void arrivingShipApplication(Ship ship) {
	if(_shipEventName != null)
	    apply(ship, _shipEventName, true);
    }
    
    public String getName() {
	return _name;
    }
    
    public String getDisplayName() {
	return Localizer.getLocalizedString("tech." + _name, "name");
    }
    
    public String getDescription() {
	return Localizer.getLocalizedString("tech." + _name, "description");
    }
    
    public String getNameForLevel(int level) {
	if(_maxLevel <= 1)
	    return getDisplayName();
	StringBuilder result = new StringBuilder(getDisplayName());
	result.append(" (Level ");
	result.append(level);
	result.append("/");
	result.append(_maxLevel);
	result.append(")");
	return result.toString();
    }
    
    public Vector2f getCenter() {
	return new Vector2f(_center);
    }
    
    public Vector2f getDimensions() {
	return new Vector2f(_dimensions);
    }
    
    public String getImagePath() {
	return _imagePath;
    }
    
}
