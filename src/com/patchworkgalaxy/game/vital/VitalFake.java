package com.patchworkgalaxy.game.vital;

/**
 * A {@link Vital} with an external value.
 * <p>
 * The current value of a VitalFake is derived from something outside the
 * vital. Modifying the current value modifies that value.
 * </p><p>
 * This class is something of a hack. The intented use is allowing game events
 * to modify things that aren't really vitals, but are specific enough
 * not to warrant their own event type.
 * </p>
 * @author redacted
 */
public abstract class VitalFake extends Vital {
    
    public VitalFake(int max) {
	super(max);
    }
    
    public VitalFake() {
	this(Integer.MAX_VALUE);
    }
    
    @Override
    public int getCurrent() {
	return getCurrentImpl();
    }
    
    @Override
    public int modify(int amount) {
	int result = modifyImpl(amount);
	modified();
	return result;
    }
    
    public abstract int getCurrentImpl();
    
    public abstract int modifyImpl(int amount);
    
}
