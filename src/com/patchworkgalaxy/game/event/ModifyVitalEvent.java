package com.patchworkgalaxy.game.event;

import com.patchworkgalaxy.game.component.GameComponent;
import com.patchworkgalaxy.game.component.GameEvent;
import com.patchworkgalaxy.game.vital.Vital;
import com.patchworkgalaxy.general.data.GameProps;
import com.jme3.math.FastMath;


/**
 * A GameEvent that modifies some vital of the recipient.
 * @author redacted
 */
public class ModifyVitalEvent extends GameEvent {
    
    public ModifyVitalEvent(GameProps props, GameComponent sender, GameComponent receiver, GameEvent cause) {
	super(props, sender, receiver, cause);
    }
    
    @Override
    protected void postImpl() {
	if(!wasCancelled()) {
	    }
        String path = props.getString("Subtype");
	    Object o = getGameState().lookup(path);
            int decrease= Math.abs(getfinalDamage());
	    ((Vital)o).modify(-decrease);
    }

    @Override
    protected void cancelImpl() {}
    
    @Override
    protected void cancelImpl2() {}
    
}
