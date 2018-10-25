package com.patchworkgalaxy.template.types;

import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.Player;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.Template;
import com.patchworkgalaxy.template.TemplateRegistry;

public class ShipTemplate extends Template<Ship> {
 
    private final boolean _heroic;
    
    public ShipTemplate(GameProps props) {
	super(props, Breed.SHIP.name());
	_heroic = props.getBoolean("Heroic!");
    }

    @Override
    public Ship instantiate(Object... params) {
	Ship s = new Ship(props, (Player)params[0]);
	String conditions = props.getString("Condition");
	if(conditions != null) {
	    for(String condition : conditions.split(","))
		s.addCondition(condition.trim());
	}
	if(params.length > 1 && params[1] instanceof Tile)
	    s.setPosition((Tile)params[1]);
	return s;
    }
    
    public ModelTemplate getModelTemplate() {
	return TemplateRegistry.MODELS.lookup(getString("Model"));
    }
    
    public boolean isHeroic() {
	return _heroic;
    }
    
}
