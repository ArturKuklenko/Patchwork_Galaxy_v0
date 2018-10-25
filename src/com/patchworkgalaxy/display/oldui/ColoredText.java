package com.patchworkgalaxy.display.oldui;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import java.util.List;

public final class ColoredText {
    
    private String _text;
    private final List<String> _coloredSections;
    private final List<ColorRGBA> _colors;
    
    public ColoredText() {
	this("");
    }
    
    public ColoredText(ColoredText copyOf) {
	_text = copyOf._text;
	_coloredSections = new ArrayList<>(copyOf._coloredSections);
	_colors = new ArrayList<>(copyOf._colors);
    }
    
    public ColoredText(String text) {
	_text = text;
	_coloredSections = new ArrayList<>();
	_colors = new ArrayList<>();
    }
    
    public ColoredText(String newText, ColorRGBA color) {
	this();
	addText(newText, color);
    }
    
    public ColoredText addText(String newText, ColorRGBA color) {
	newText = newText.replace("\\n", "\n");
	_text += newText;
	if(color != null) {
	    _coloredSections.add("\\Q" + newText + "\\E");
	    _colors.add(color);
	}
	return this;
    }
    
    public ColoredText addText(String newText) {
	addText(newText, ColorRGBA.White);
	return this;
    }
    
    public ColoredText reset() {
	_text = "";
	_coloredSections.removeAll(_coloredSections);
	_colors.removeAll(_colors);
	return this;
    }
    
    public void setBitmapTextTo(BitmapText bitmapText) {
	bitmapText.setText(_text);
	for(int i = _coloredSections.size(); --i >= 0;) {
	    bitmapText.setColor(_coloredSections.get(i), _colors.get(i));
	}
    }
    
    public void spliceOtherText(ColoredText other, String separator) {
	_text += separator + other._text;
	_coloredSections.addAll(other._coloredSections);
	_colors.addAll(other._colors);
    }
    
    @Override
    public String toString() {
	return _text;
    }
    
}
