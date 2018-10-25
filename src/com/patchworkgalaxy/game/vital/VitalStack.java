/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patchworkgalaxy.game.vital;

/**
 * A Vital composed of other Vitals.
 * <p>A VitalStack doesn't meaningfully have its own maximum or current values. Instead, it is a stack of other vitals, from which those values are derived.</p>
 * <p>A VitalStack's maximum value is equal to the sum of the maximum values of the stack. Its current value is either the sum of the stack's current values or the first vital's current values.</p>
 * <p>Modifications to a VitalStack's current value propogate down the stack. As much change as possible is made to the top one. Any leftover change is applies to the second-top one, and so on.</p>
 * @author Patrick
 */
public class VitalStack extends Vital {
    
    final Vital[] stack;
    final int size;
    final boolean currentFromFirst;
    
    public VitalStack(boolean currentFromFirst, Vital... stack) {
	super(getMax(stack));
	this.currentFromFirst = currentFromFirst;
	this.stack = stack;
	size = stack.length;
	updateCurrent();
    }
    
    private static int getMax(Vital[] stack) {
	int m = 0;
	for(Vital v : stack) {
	    m += v.max;
	}
	return m;
    }
    
    @Override
    public int damage(int amount) {
        int i = size;
	while(--i >= 0 && amount != 0)
	    amount = stack[i].damage(amount);
	updateCurrent();
        return amount;
    }
    
    @Override
    public int regenerate(int amount) {
        int i = size;
	while(--i >= 0 && amount != 0)
	    amount = stack[i].regenerate(amount);
	updateCurrent();
        return amount;
    }
    
    private void updateCurrent() {
	if(!currentFromFirst) {
	    int c = 0;
	    for(Vital v : stack) 
		c += v.current;
	    current = c;
	}
	else
	    current = stack[0].current;
	modified();
    }
    
}
