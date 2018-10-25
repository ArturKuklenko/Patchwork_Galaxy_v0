package com.patchworkgalaxy.display.ui.controller;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.UX2DControl;

public class Property<T> {
    
    private final OldStateFactory<T> _oldStateFactory;
    private final OldControlMutator _oldControlMutator;
    
    public static final Type<Vector2f>
	    CENTER = new Type<>(OldStateFactory.VEC2.CENTER),
	    SIZE = new Type<>(OldStateFactory.VEC2.SIZE);
    public static final Type<Float>
	    TEXT_SIZE = new Type<>(OldStateFactory.NUMBER.FONT_SIZE),
	    OPACITY = new Type<>(OldStateFactory.NUMBER.OPACITY),
	    Z_INDEX = new Type<>(OldStateFactory.NUMBER.Z_INDEX);
    public static final Type<String>
	    BACKGROUND = new Type<>(OldStateFactory.STRING.BACKGROUND);
    public static final Type<ColoredText>
	    TEXT = new Type<>(OldStateFactory.COLORTEXT.TEXT),
	    TOOLTIP_TEXT = new Type<>(OldStateFactory.COLORTEXT.TOOLTIP_TEXT);
    public static final Type<BitmapFont.Align>
	    TEXT_ALIGN = new Type<>(OldStateFactory.ALIGN.TEXT_ALIGN);
    public static final Type<BitmapFont.VAlign>
	    TEXT_VALIGN = new Type<>(OldStateFactory.VALIGN.TEXT_VALIGN);
    
    public static class Type<T> {
	private final OldStateFactory<T> _oldStateFactory;
	private Type(OldStateFactory<T> oldStateFactory) {
	    _oldStateFactory = oldStateFactory;
	}
	public Property<T> getProperty(UX2DControl oldControl, float duration) {
	    return new Property<>(oldControl, _oldStateFactory, duration);
	}
	public static Type[] values() {
	    return new Type[] {
		CENTER, SIZE, TEXT_SIZE, OPACITY, Z_INDEX, BACKGROUND, TEXT, TOOLTIP_TEXT, TEXT_ALIGN, TEXT_VALIGN
	    };
	}
    }
    
    Property(UX2DControl oldControl, OldStateFactory<T> oldStateFactory, float transitionDuration) {
	_oldStateFactory = oldStateFactory;
	_oldControlMutator = new OldControlMutator(oldControl, transitionDuration);
    }
    
    public void write(T value) {
	if(value instanceof Float && Float.isNaN((Float)value))
	    return;
	if(value != null)
	    _oldControlMutator.mutate(_oldStateFactory.getControlState(value));
    }
    
}
