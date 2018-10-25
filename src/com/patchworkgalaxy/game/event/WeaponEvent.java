package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.misc.Formula;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Weapon;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.TemplateRegistry;

public class WeaponEvent extends GameEvent {
    
    public WeaponEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	//Weapon w = TemplateRegistry.WEAPONS.lookup(props.getString("Weapon")).instantiate(sender, receiver, wasCancelled());
	Weapon w = TemplateRegistry.WEAPONS.lookup(props.getString("Weapon")).instantiate(sender, receiver, wasCancelled());

        magicAddWeapon(w);
	//if(!wasCancelled())
	    w.impact(-getMagnitude());
	
    }

    @Override
    protected void cancelImpl() {}
    
    @Override
    protected void cancelImpl2() {
	if((props.getInt("Flags") & 2) != 0) {
	    float miracle = Formula.KINETIC_MIRACLE_CHANCE.toFloat(getGameState());
	    if(successChance < miracle) {
		successChance = miracle;
	    }
	}
    }
    
}
