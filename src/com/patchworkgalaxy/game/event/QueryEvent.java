package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Numeric;

public class QueryEvent extends GameEvent implements GameEvent.AlwaysVirtual {

    private float _result = Float.NaN;
    
    public QueryEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	Object o = receiver.lookup(props.getString("Subtype"));
	if(o instanceof Numeric)
	    _result = ((Numeric)o).toFloat(getGameState());
	else if(o instanceof Number)
	    _result = ((Number)o).floatValue();
	_result += getMagnitude();
	if(Float.isNaN(_result))
	    _result = 0;
    }

    @Override
    protected void cancelImpl() {}

    @Override
    protected void cancelImpl2() {}

    @Override
    public float toFloat(GameState gameState) {
	enqueue();
	return _result;
    }
    
}
