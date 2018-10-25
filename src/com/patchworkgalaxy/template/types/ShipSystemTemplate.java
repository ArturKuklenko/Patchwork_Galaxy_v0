package com.patchworkgalaxy.template.types;

import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.Template;

public class ShipSystemTemplate extends Template<ShipSystem> {

    public ShipSystemTemplate(GameProps props) {
	super(props, Breed.SYSTEM.name());
    }
    
    @Override
    public ShipSystem instantiate(Object... params) {
	return new ShipSystem(this, (Ship)params[0]);
    }
    
}
