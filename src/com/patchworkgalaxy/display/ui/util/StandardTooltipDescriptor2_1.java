/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patchworkgalaxy.display.ui.util;

import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.descriptors.TooltipDescriptor;

public class StandardTooltipDescriptor2_1 implements TooltipDescriptor {
    
    private float _tooltipWidth;
    private ColoredText _tooltipText;
    
    private static final float
	    DEFAULT_TOOLTIP_WIDTH = 200f,
	    DEFAULT_LONG_TOOLTIP_WIDTH = 300f;
    private static final int
	    LONG_TOOLTIP_THRESHOLD = 50;
    
    public StandardTooltipDescriptor2_1(String text) {
	_tooltipWidth = Float.NaN;
	_tooltipText = new ColoredText(text);
    }
    
    public StandardTooltipDescriptor2_1(ColoredText text) {
	_tooltipText = text;
    }
    
    public StandardTooltipDescriptor2_1 setWidth(float width) {
	_tooltipWidth = width;
	return this;
    }

    @Override public float getTooltipWidth() {
	if(Float.isNaN(_tooltipWidth)) {
	    ColoredText tooltipText = getTooltipText();
	    int len = (tooltipText == null) ? 0 : tooltipText.toString().length();
	    return (len >= LONG_TOOLTIP_THRESHOLD) ? DEFAULT_LONG_TOOLTIP_WIDTH : DEFAULT_TOOLTIP_WIDTH;
	}
	return _tooltipWidth;
    }

    @Override public ColoredText getTooltipText() {
        System.out.println("tooltip");
	return _tooltipText;
    }
    
}
