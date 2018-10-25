package com.patchworkgalaxy.display.models;

import com.jme3.scene.Spatial;

class Triggers {
    
    private Triggers() {}
    
    private static interface Trigger {
	void init(Animation animation);
	void done(Animation animation);
	void special(Animation animation);
    }
    
    static void handleTrigger(Animation animation, boolean starting) {
	Trigger t = getTrigger(animation.getTrigger());
	if(t != null) {
	    if(starting)
		t.init(animation);
	    else
		t.done(animation);
	}
    }
    
    static void handleSpecialTrigger(Animation animation) {
	Trigger t = getTrigger(animation.getTrigger());
	if(t != null)
	    t.special(animation);
    }
    
    private static Trigger getTrigger(String trigger) {
	switch(trigger) {
	case "Arrival":
	    return ARRIVAL;
	case "Death":
	    return DEATH;
	default:
	    return null;
	}
    }
    
    private static final Trigger ARRIVAL = new Trigger() {
	@Override
	public void init(Animation animation) {
	    animation.model.getSpatial().setCullHint(Spatial.CullHint.Always);
	}
	@Override
	public void special(Animation animation) {
	    animation.model.getSpatial().setCullHint(Spatial.CullHint.Inherit);
	}
	@Override
	public void done(Animation animation) {}
    };
    
    private static final Trigger DEATH = new Trigger() {
	@Override
	public void init(Animation animation) {
        }
	
	@Override
	public void special(Animation animation) {
	    animation.model.getSpatial().setCullHint(Spatial.CullHint.Always);
        }
	
	@Override
	public void done(Animation animation) {
	    animation.model.setVisible(false);
	}
    };
    
}
