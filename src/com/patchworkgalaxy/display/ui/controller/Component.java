package com.patchworkgalaxy.display.ui.controller;

import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.ComponentState;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;

public class Component implements Subscriber<Object> {
    
    private Panel _panel;
    private final UX2DControl _oldControl;
    private final ComponentProperties _properties;
    private final ComponentState _initialState;
    private final ComponentDescriptor _descriptor;
    
    @SuppressWarnings("unchecked")
    public static Component create(ComponentDescriptor descriptor, UX2DControl control) {
	Component result = new Component(descriptor, control);
	ComponentCallbackManager.setupCallbacks(result, control, descriptor.getCallbacks());
	for(Subscribable topic : descriptor.getSubscribeTo())
	    topic.addSubscription(result);
	return result;
    }
    
    private Component(ComponentDescriptor descriptor, UX2DControl oldControl) {
	_oldControl = oldControl;
	_properties = new ComponentProperties(oldControl, descriptor.getTransitionDurations());
	_initialState = descriptor;
	_descriptor = descriptor;
    }
    
    void setPanel(Panel panel) {
	assert(panel == null);
	_panel = panel;
    }
    
    public Panel getPanel() {
	return _panel;
    }
    
    @SafeVarargs
    public final <T> float write(T value, Property.Type<T>... keys) {
	float duration = 0, foo;
	for(Property.Type<T> key : keys) {
	    foo = _properties.write(key, value);
	    duration = Math.max(duration, foo);
	}
	return duration;
    }
    
    public void write(ComponentState componentState) {
	write(componentState.getBackgroundImage(), Property.BACKGROUND);
	write(componentState.getCenter(), Property.CENTER);
	write(componentState.getOpacity(), Property.OPACITY);
	write(componentState.getSize(), Property.SIZE);
	write(componentState.getText(), Property.TEXT);
	write(componentState.getTextSize(), Property.TEXT_SIZE);
	write(componentState.getZIndex(), Property.Z_INDEX);
    }
    
    public void reset() {
	write(_initialState);
    }
    
    String getId() {
	return _oldControl.getName();
    }
    
    public void setInputText(String text) {
	_oldControl.pipeTextToInput(text);
    }
    
    public String getInputText() {
	return _oldControl.getInputText();
    }

    @Override public void update(Subscribable topic, Object message) {
	UICallback callback = _oldControl.getCallback(CallbackType.OBSERVE);
	if(callback != null)
	    callback.callback(_oldControl);
    }
    
    ComponentDescriptor getDescriptor() {
	return _descriptor;
    }
    
    public void focus() {
	_oldControl.focus();
    }
    
}
