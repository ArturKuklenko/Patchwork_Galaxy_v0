package com.patchworkgalaxy.display.oldui;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;

public class ControlState {
    
    Vector2f _center, _dimensions, _offset;
    Float _zIndex, _opacity, _fontSize, _tooltipWidth;
    ColoredText _text, _tooltipText;
    String _background;
    BitmapFont.Align _textAlign;
    BitmapFont.VAlign _textVAlign;
    Character _hotkey;
    boolean _valid;
    
    public ControlState() {
	_opacity = 1f;
	_fontSize = 12f;
	_valid = true;
    }
    
    public ControlState(ControlState copyOf) {
	_center = copyOf._center;
	_dimensions = copyOf._dimensions;
	_offset = copyOf._offset;
	_zIndex = copyOf._zIndex;
	_opacity = copyOf._opacity;
	_fontSize = copyOf._fontSize;
	_text = copyOf._text;
	_tooltipText = copyOf._tooltipText;
	_tooltipWidth = copyOf._tooltipWidth;
	_background = copyOf._background;
	_textAlign = copyOf._textAlign;
	_textVAlign = copyOf._textVAlign;
	_hotkey = copyOf._hotkey;
	_valid = copyOf._valid;
    }
    
    public static ControlState recent() {
	return UX2D.getInstance().recentControl().getDefaultState();
    }
    
    public static ControlState forControl(String key) {
	return UX2D.getInstance().getControl(key).getDefaultState();
    }
    
    public ControlState setCenter(Vector2f center) {
	if(center == null)
	    _center = null;
	else
	    _center = new Vector2f(center);
	return this;
    }
    
    public ControlState setDimensions(Vector2f dimensions) {
	if(dimensions == null)
	    _dimensions = null;
	else
	    _dimensions = new Vector2f(dimensions);
	return this;
    }
    
    public ControlState setOffset(Vector2f offset) {
	if(offset == null)
	    _offset = null;
	else
	    _offset = new Vector2f(offset);
	return this;
    }
    
    public ControlState setZIndex(float zIndex) {
	if(Float.isNaN(zIndex))
	    _zIndex = null;
	else
	    _zIndex = zIndex;
	return this;
    }
    
    public ControlState setOpacity(float opacity) {
	if(Float.isNaN(opacity))
	    _opacity = null;
	else
	    _opacity = opacity;
	return this;
    }
    
    public ControlState setFontSize(float fontSize) {
	if(Float.isNaN(fontSize))
	    _fontSize = null;
	else
	    _fontSize = fontSize;
	return this;
    }
    
    public ControlState setText(ColoredText text) {
	if(text == null)
	    _text = null;
	else
	    _text = new ColoredText(text);
	return this;
    }
    
    public ControlState setText(String text) {
	if(text == null)
	    _text = null;
	else
	    _text = new ColoredText(text);
	return this;
    }
    
    public ControlState setTooltipText(ColoredText text) {
	_tooltipText = text;
	return this;
    }
    
    public ControlState setTooltipWidth(float width) {
	if(Float.isNaN(width))
	    _tooltipWidth = null;
	else
	    _tooltipWidth = width;
	return this;
    }
    
    public ControlState setBackground(String background) {
	_background = background;
	return this;
    }
    
    public ControlState setTextAlign(BitmapFont.Align textAlign) {
	_textAlign = textAlign;
	return this;
    }
    
    public ControlState setTextVAlign(BitmapFont.VAlign textVAlign) {
	_textVAlign = textVAlign;
	return this;
    }
    
    public ControlState setHotkey(char hotkey) {
	if(hotkey > 0)
	    _hotkey = hotkey;
	return this;
    }
    
    public ControlState invalidate() {
	_valid = false;
	return this;
    }
    
}
