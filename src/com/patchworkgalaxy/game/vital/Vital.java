package com.patchworkgalaxy.game.vital;

import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.general.data.Numeric;
import java.util.Observable;

public class Vital extends Observable implements Cloneable, Numeric, Namespace {
    
    int max;
    int current;
    
    public Vital(int max) {
        this.max = max;
        this.current = max;
    }
    
    /**
     * Creates a vital with no limit and initial value 0.
     * @return the vital
     */
    public static Vital unlimited() {
	Vital vital = new Vital(Integer.MAX_VALUE);
	vital.current = 0;
	return vital;
    }
    
    public int getCurrent() {
	return current;
    }
    
    public int getMax() {
	return max;
    }
    
    public int damage(int amount) {
        if(amount < current) {
            current -= amount;
            amount = 0;
        }
        else {
            amount -= current;
            current = 0;
        }
	modified();
        return amount;
    }
    
    public int regenerate(int amount) {
        if(current + amount > max) {
            amount -= (max - current);
            current = max;
	    modified();
            return amount;
        }
        else {
            current += amount;
	    modified();
            return 0;
        }
    }
    
    public int damage() {
	return damage(current);
    }
    
    public int regenerate() {
        return regenerate(max);
    }
    
    public int modify(int amount) {
	if(amount > 0)
	    return regenerate(amount);
	else
	    return damage(-amount);
    }
    
    public void modifyMaximum(int amount) {
	max += amount;
	if(max < 0)
	    max = 0;
	if(current > max)
	    current = max;
	modified();
    }

    final void modified() {
	setChanged();
	notifyObservers();
    }
    
    Vital maxAsVital() {
	return new VitalFake(max) {
	    @Override
	    public int getCurrentImpl() {
		return Vital.this.max;
	    }
	    @Override
	    public int modifyImpl(int amount) {
		Vital.this.modifyMaximum(amount);
		return 0;
	    }
	};
    }
    
    @Override public Object lookup(String name) {
	switch (name) {
	case "current":
	    return (float)getCurrent();
	case "max":
	    return maxAsVital();
	default:
	    return null;
	}
    }

    @Override public float toFloat(GameState gameState) {
	return getCurrent();
    }
    
    public double getPercent() {
	return (float)current / (float)max;
    }
    
}
