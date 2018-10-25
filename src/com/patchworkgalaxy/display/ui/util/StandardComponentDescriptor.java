package com.patchworkgalaxy.display.ui.util;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.TextInputDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.TooltipDescriptor;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StandardComponentDescriptor implements ComponentDescriptor {
    
    private final String _name;
    private TextInputDescriptor _textInputDescriptor;
    private BitmapFont.Align _textAlignment;
    private BitmapFont.VAlign _textVAlignment;
    private TooltipDescriptor _tooltipDescriptor;
    private Map<Property.Type, Float> _transitions;
    private Set<ComponentCallback> _callbacks;
    private List<Subscribable> _subscribeTo;
    private char _hotkey;
    private final Vector2f _center, _size;
    private String _backgroundImage;
    private float _opacity;
    private float _zIndex;
    private ColoredText _text;
    private float _textSize;
    
    public StandardComponentDescriptor(Vector2f center, Vector2f size) {
	this(null, center, size);
    }
    
    public StandardComponentDescriptor(String name, Vector2f center, Vector2f size) {
	_name = name;
	_transitions = new HashMap<>();
	_callbacks = new HashSet<>();
	_center = new Vector2f(center);
	_size = new Vector2f(size);
	_text = new ColoredText();
	_opacity = 1f;
	_textSize = 12f;
	_subscribeTo = new ArrayList<>();
    }

    @Override public String getName() {
	return _name;
    }

    @Override public TextInputDescriptor getTextInputDescriptor() {
	return _textInputDescriptor;
    }

    @Override public BitmapFont.Align getTextAlignment() {
	return _textAlignment;
    }

    @Override public BitmapFont.VAlign getTextVAlignment() {
	return _textVAlignment;
    }

    @Override public TooltipDescriptor getTooltipDescriptor() {
	return _tooltipDescriptor;
    }

    @Override public Map<Property.Type, Float> getTransitionDurations() {
	return _transitions;
    }

    @Override public Set<ComponentCallback> getCallbacks() {
	return _callbacks;
    }

    @Override public char getHotkey() {
	return _hotkey;
    }
    
    @Override public Vector2f getCenter() {
	return _center;
    }

    @Override public Vector2f getSize() {
	return _size;
    }

    @Override public String getBackgroundImage() {
	return _backgroundImage;
    }

    @Override public float getOpacity() {
	return _opacity;
    }

    @Override public float getZIndex() {
	return _zIndex;
    }

    @Override public ColoredText getText() {
	return _text;
    }

    @Override public float getTextSize() {
	return _textSize;
    }

    @Override public List<Subscribable> getSubscribeTo() {
	return _subscribeTo;
    }
    
    public StandardComponentDescriptor setTextInputDescriptor(TextInputDescriptor textInputDescriptor) {
        _textInputDescriptor = textInputDescriptor;
        return this;
    }

    public StandardComponentDescriptor setTextAlignment(BitmapFont.Align textAlignment) {
        _textAlignment = textAlignment;
        return this;
    }
    
    public StandardComponentDescriptor setTextAlignLeft() {
	return setTextAlignment(BitmapFont.Align.Left);
    }
    
    public StandardComponentDescriptor setTextAlignCenter() {
	return setTextAlignment(BitmapFont.Align.Center);
    }
    
    public StandardComponentDescriptor setTextAlignRight() {
	return setTextAlignment(BitmapFont.Align.Right);
    }

    public StandardComponentDescriptor setTextVAlignment(BitmapFont.VAlign textVAlignment) {
        _textVAlignment = textVAlignment;
        return this;
    }
    
    public StandardComponentDescriptor setTextVAlignTop() {
	return setTextVAlignment(BitmapFont.VAlign.Top);
    }
    
    public StandardComponentDescriptor setTextVAlignCenter() {
	return setTextVAlignment(BitmapFont.VAlign.Center);
    }
    
    public StandardComponentDescriptor setTextVAlignBottom() {
	return setTextVAlignment(BitmapFont.VAlign.Bottom);
    }
    
    public StandardComponentDescriptor setTooltipDescriptor(TooltipDescriptor tooltipDescriptor) {
        _tooltipDescriptor = tooltipDescriptor;
        return this;
    }
    
    public StandardComponentDescriptor setTooltip(ColoredText tooltip) {
	return setTooltipDescriptor(new StandardTooltipDescriptor(tooltip));
    }
    
    public StandardComponentDescriptor setTooltip(String tooltip) {
	return setTooltipDescriptor(new StandardTooltipDescriptor(tooltip));
    }

    public StandardComponentDescriptor setHotkey(char hotkey) {
        _hotkey = hotkey;
        return this;
    }

    public StandardComponentDescriptor setCenter(Vector2f center) {
        _center.set(center);
        return this;
    }

    public StandardComponentDescriptor setSize(Vector2f size) {
        _size.set(size);
        return this;
    }

    public StandardComponentDescriptor setBackgroundImage(String backgroundImage) {
        _backgroundImage = backgroundImage;
        return this;
    }

    public StandardComponentDescriptor setOpacity(float opacity) {
        _opacity = opacity;
        return this;
    }

    public StandardComponentDescriptor setZIndex(float zIndex) {
        _zIndex = zIndex;
        return this;
    }
    
    public StandardComponentDescriptor setText(String text) {
	return setText(new ColoredText(text));
    }

    public StandardComponentDescriptor setText(ColoredText text) {
        _text = text;
        return this;
    }

    public StandardComponentDescriptor setTextSize(float textSize) {
        _textSize = textSize;
        return this;
    }
    
    public StandardComponentDescriptor addCallback(ComponentCallback callback) {
	_callbacks.add(callback);
	return this;
    }
    
    public StandardComponentDescriptor addTransition(float duration, Property.Type... types) {
	for(Property.Type type : types)
	    _transitions.put(type, duration);
	return this;
    }
    
    public StandardComponentDescriptor addSubscription(Subscribable topic) {
	_subscribeTo.add(topic);
	return this;
    }
    
    @Override public void onShow(Component component) {}
    
    @Override public void onHide(Component component) {}
    
}