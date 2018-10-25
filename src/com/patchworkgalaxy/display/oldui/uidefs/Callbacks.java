package com.patchworkgalaxy.display.oldui.uidefs;

import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import static com.patchworkgalaxy.display.oldui.uidefs.ControlStates.*;

public class Callbacks {
    
    private Callbacks() {}
    
    public void combine(UX2DControl control, CallbackType type, UICallback add) {
	UICallback existing = control.getCallback(type);
	if(existing == null)
	    control.setCallback(type, add);
	else
	    control.setCallback(type, combination(existing, add));
    }
    
    public static UICallback combination(final UICallback... callbacks) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		for(UICallback callback : callbacks)
		    callback.callback(control);
	    }
	};
    }
    
    public static UICallback timeout(final float duration) {
	return new UICallback() {
	    private float _duration = duration;
	    @Override
	    public void callback(UX2DControl control) {
		_duration -= control.getTPF();
		if(_duration < 0)
		    control.setDead();
	    }
	};
    }
    
    public static UICallback toggle(UICallback callback1, UICallback callback2) {
	return toggle(null, 0f, callback1, callback2);
    }
    
    public static UICallback toggle(final ControlState altState, final float duration, final UICallback callback1, final UICallback callback2) {
	return new UICallback() {
	    boolean swapped = false;
	    @Override
	    public void callback(UX2DControl control) {
		if(swapped) {
		    swapped = false;
		    callback2.callback(control);
		    if(altState != null)
			control.changeStateWithDuration(null, duration);
		}
		else {
		    swapped = true;
		    callback1.callback(control);
		    if(altState != null)
			control.changeStateWithDuration(altState, duration);
		}
	    }
	};
    }
    
    public static UICallback swapTo(final ControlState altState, final float duration, final UICallback altCallback) {
	return new UICallback() {
	    boolean swapped = false;
	    @Override
	    public void callback(UX2DControl control) {
		if(swapped)
		    altCallback.callback(control);
		else {
		    swapped = true;
		    control.changeStateWithDuration(altState, duration);
		}
	    }
	};
    }
    
    public static UICallback stateChanger(final String key, final ControlState state) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		UX2D.getInstance().updateControl(key, state);
	    }
	};
    }
    
    public static UICallback stateChangerWithDuration(final String key, final ControlState state, final float duration) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		UX2D.getInstance().updateControlWithDuration(key, state, duration);
	    }
	};
    }
    
    public static UICallback stateChangerWithSpeed(final String key, final ControlState state, final float speed) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		UX2D.getInstance().updateControlWithSpeed(key, state, speed);
	    }
	};
    }
    
    public static UICallback textChanger(String key, String text) {
	return textChanger(key, new ColoredText(text));
    }
    
    public static UICallback textChanger(final String key, final ColoredText text) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		UX2D.getInstance().setControlText(key, text);
	    }
	};
    }
    
    public static UICallback flashDim(ControlState base, final float amount, final float duration) {
	final ControlState base2 = (base == null ? new ControlState() : base);
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		control.changeState(DIM(base2, amount));
		control.delayStateChange(base2, duration);
		control.flash();
	    }
	};
    }
    
    public static UICallback showText(String key, String text) {
	return showText(key, new ColoredText(text));
    }
    
    public static UICallback showText(final String key, final ColoredText text) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		UX2D.getInstance().setControlText(key, text);
	    }
	};
    }
    
}
