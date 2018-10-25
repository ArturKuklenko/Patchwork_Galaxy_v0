package com.patchworkgalaxy.template.types;

import com.patchworkgalaxy.game.component.Breed;
import com.patchworkgalaxy.game.component.Condition;
import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.event.ApplyConditionEvent;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.Template;
import com.patchworkgalaxy.template.parser.CanBeEvent;

public class ConditionTemplate extends Template<Condition> implements CanBeEvent {

    public ConditionTemplate(GameProps props) {
	super(props, Breed.CONDITION.name());
    }
    
    @Override
    public Condition instantiate(Object... params) {
	return new Condition(props, (GameComponent)params[0]);
    }

    @Override
    public GameEvent toEvent(Object... params) {
	GameEvent cause = params.length > 2 ? (GameEvent)params[2] : null;
	return new ApplyConditionEvent(props, (GameComponent)params[0], (GameComponent)params[1], cause);
    }
    
}
