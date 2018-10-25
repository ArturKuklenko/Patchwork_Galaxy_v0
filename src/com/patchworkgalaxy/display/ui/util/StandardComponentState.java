package com.patchworkgalaxy.display.ui.util;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.descriptors.ComponentState;

public class StandardComponentState implements ComponentState {
    private Vector2f _center, _size;
    private String _backgroundImage;
    private float _opacity;
    private float _zIndex;
    private ColoredText _text;
    private float _textSize;
    
    public StandardComponentState() {
	_center = null;
	_size = null;
	_text = null;
	_opacity = Float.NaN;
	_textSize = Float.NaN;
	_zIndex = Float.NaN;
    }
    
    @Override
    public Vector2f getCenter() {
	return _center;
    }

    @Override
    public Vector2f getSize() {
	return _size;
    }

    @Override
    public String getBackgroundImage() {
	return _backgroundImage;
    }

    @Override
    public float getOpacity() {
	return _opacity;
    }

    @Override
    public float getZIndex() {
	return _zIndex;
    }

    @Override
    public ColoredText getText() {
	return _text;
    }

    @Override
    public float getTextSize() {
	return _textSize;
    }
    
    public StandardComponentState setCenter(float x, float y) {
	return setCenter(new Vector2f(x, y));
    }

    public StandardComponentState setCenter(Vector2f center) {
	if(_center == null) _center = new Vector2f();
        _center.set(center);
        return this;
    }
    
    public StandardComponentState setSize(float w, float h) {
	return setSize(new Vector2f(w, h));
    }
    
    public StandardComponentState setSize(Vector2f size) {
	if(_size == null) _size = new Vector2f();
        _size.set(size);
        return this;
    }

    public StandardComponentState setBackgroundImage(String backgroundImage) {
        _backgroundImage = backgroundImage;
        return this;
    }

    public StandardComponentState setOpacity(float opacity) {
        _opacity = opacity;
        return this;
    }

    public StandardComponentState setZIndex(float zIndex) {
        _zIndex = zIndex;
        return this;
    }

    public StandardComponentState setText(ColoredText text) {
        _text = text;
        return this;
    }

    public StandardComponentState setTextSize(float textSize) {
        _textSize = textSize;
        return this;
    }
    
}