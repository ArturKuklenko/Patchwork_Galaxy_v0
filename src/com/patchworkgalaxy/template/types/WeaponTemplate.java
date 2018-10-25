package com.patchworkgalaxy.template.types;

import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.component.Weapon;
import com.patchworkgalaxy.game.event.WeaponEvent;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.Template;
import com.patchworkgalaxy.template.parser.CanBeEvent;

public class WeaponTemplate extends Template<Weapon> implements CanBeEvent {

    public WeaponTemplate(GameProps props) {
	super(props, Breed.WEAPON.name());
    }

    @Override
    public Weapon instantiate(Object... params) {
	GameComponent sender = (GameComponent)params[0];
	GameComponent receiver = (GameComponent)params[1];
	boolean missed = params.length > 2 ? (boolean)params[2] : false;
        missed = false;
	return new Weapon(props, sender, receiver, missed);
    }

    @Override
    public GameEvent toEvent(Object... params) {
	GameComponent sender = (GameComponent)params[0];
	GameComponent receiver = (GameComponent)params[1];
	GameEvent cause = params.length > 2 ? (GameEvent)params[2] : null;
	String name = props.getString("Name");
	GameProps props2 = props.mutable().set("Weapon", name);
	return new WeaponEvent(props2, sender, receiver, cause);
    }
    
}
