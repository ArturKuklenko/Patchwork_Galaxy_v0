package com.patchworkgalaxy.display.oldui;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.Definitions;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public final class TextInput {
    
    private final UX2DControl _control;
    private final boolean _resetOnBlur;
    private final String _prefix, _prompt;
    private final boolean _obscure;
    private String _text = "";
    
    private static final float BLINK_DURATION = .5f;
    private static float _blinker = BLINK_DURATION;
        
    private TextInput(UX2DControl control, String prefix, String prompt, boolean resetOnBlur, boolean obscure) {
	_control = control;
	_resetOnBlur = resetOnBlur;
	_prefix = prefix;
	_prompt = prompt;
	_obscure = obscure;
    }
    
    void paste() {
	try {
	    Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	    if(t != null) {
		String text = (String)t.getTransferData(DataFlavor.stringFlavor);
		text = text.replaceAll("\\p{Cntrl}", "");
		_text += text;
		updateControl();
	    }
	}
	catch(IllegalStateException | NullPointerException | IOException | UnsupportedFlavorException e) {}
    }
    
    void type(char c) {
	if(c == VirtualKeyboard.TAB) {
	    _control.getUX2D().tabFocus(_control);
	    return;
	}
	_blinker = 0;
	if(c > 31 && c < 127) {
	    _text += c;
	    updateControl();
	}
	else if(c == VirtualKeyboard.BACKSPACE) {
	    if(_text.length() > 0) {
		_text = _text.substring(0, _text.length() - 1);
		updateControl();
	    }
	}
	else if(c == Definitions.KEY_ESC)
	    _control.blur();
	else if(c == VirtualKeyboard.NEWLINE)
	    _control.doCallback(CallbackType.SUBMIT);
    }
    
    void updateControl() {
	String text = _obscure ? _text.replaceAll(".", "*") : _text;
	if(_blinker <= 0 && _control.hasFocus())
	    text += "_";
	ColoredText ctext = new ColoredText(_prefix);
	    if(_control.hasFocus())
		ctext.addText(_prompt + text, ColorRGBA.Green);
	    else
		ctext.addText(text);
	_control.setText(ctext);
    }
    
    void setText(String s) {
	_text = s;
	updateControl();
    }
    
    String getText() {
	return _text;
    }
    
    void onBlur() {
	if(_resetOnBlur)
	    _text = "";
	updateControl();
	_blinker = 0;
    }
    
    void update(float tpf) {
	if(_control.hasFocus())
	    _blinker -= tpf;
	updateControl();
	if(_blinker < -BLINK_DURATION)
	    _blinker = BLINK_DURATION;
    }
    
    public static final class Factory {
	private final boolean _resetOnBlur, _obscure;
	private final String _prefix, _prompt;
	
	public Factory(String prefix, String prompt, boolean resetOnBlur, boolean obscure) {
	    _prefix = (prefix == null) ? "" : prefix;
	    _prompt = (prompt == null) ? "" : prompt;
	    _resetOnBlur = resetOnBlur;
	    _obscure = obscure;
	}
	
	final TextInput create(UX2DControl control) {
	    return new TextInput(control, _prefix, _prompt, _resetOnBlur, _obscure);
	}
	
    }
    
}
