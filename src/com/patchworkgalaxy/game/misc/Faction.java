package com.patchworkgalaxy.game.misc;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipTemplate;

public class Faction {
    
    private final GameProps _props;
    
    public Faction(GameProps props) {
	_props = props;
    }
    
    public String getName() {
	return _props.getString("Name");
    }
    
    public boolean hasShip(String key) {
	return _props.getString("Ship").contains(key);
    }
    
    public boolean hasTech(String key) {
	return _props.getString("Tech").contains(key);
    }
   
    public ShipTemplate[] getShipTemplates() {
	String[] names = getShipNames();
	ShipTemplate[] result = new ShipTemplate[names.length];
	for(int i = 0; i < result.length; ++i)
	    result[i] = TemplateRegistry.SHIPS.lookup(names[i]);
	return result;
    }
    
    public String[] getShipNames() {
	return _props.getString("Ship").split(",");
    }
    
    public String[] getTechNames() {
	return _props.getString("Tech").split(",");
    }
    
    public ShipTemplate getHQTemplate() {
	return TemplateRegistry.SHIPS.lookup(getShipNames()[0]);
    }
    
}
