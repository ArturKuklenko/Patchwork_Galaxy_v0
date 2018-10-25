package com.patchworkgalaxy.display.ui.controller;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.ControlState;

interface OldStateFactory<T> {
    
    ControlState getControlState(T value);
    
    static class ControlStateInternal extends ControlState {
	ControlStateInternal() {
	    setOpacity(Float.NaN);
	    setFontSize(Float.NaN);
	}
    }
    
    enum VEC2 implements OldStateFactory<Vector2f> {
	CENTER {
	    @Override public ControlState getControlState(Vector2f value) {
		return new ControlStateInternal().setCenter(value);
	    }
	},
	SIZE {
	    @Override public ControlState getControlState(Vector2f value) {
		return new ControlStateInternal().setDimensions(value);
	    }
	};
    }
    
    enum NUMBER implements OldStateFactory<Float> {
	FONT_SIZE {
	    @Override public ControlState getControlState(Float value) {
		return new ControlStateInternal().setFontSize(value);
	    }
	},
	OPACITY {
	    @Override public ControlState getControlState(Float value) {
		return new ControlStateInternal().setOpacity(value);
	    }
	},
	Z_INDEX {
	    @Override public ControlState getControlState(Float value) {
		return new ControlStateInternal().setZIndex(value);
	    }
	},
    }
    
    enum STRING implements OldStateFactory<String> {
	BACKGROUND {
	    @Override public ControlState getControlState(String value) {
		return new ControlStateInternal().setBackground(value);
	    }
	},
    }
    
    enum COLORTEXT implements OldStateFactory<ColoredText> {
	TOOLTIP_TEXT {
	    @Override public ControlState getControlState(ColoredText value) {
		return new ControlStateInternal().setTooltipText(value);
	    }
	},
	TEXT {
	    @Override public ControlState getControlState(ColoredText value) {
		return new ControlStateInternal().setText(value);
	    }
	},
    }
    
    enum ALIGN implements OldStateFactory<BitmapFont.Align> {
	TEXT_ALIGN {
	    @Override public ControlState getControlState(BitmapFont.Align value) {
		return new ControlStateInternal().setTextAlign(value);
	    }
	},
    }
    
    enum VALIGN implements OldStateFactory<BitmapFont.VAlign> {
	TEXT_VALIGN {
	    @Override public ControlState getControlState(BitmapFont.VAlign value) {
		return new ControlStateInternal().setTextVAlign(value);
	    }
	},
    }
    
}
