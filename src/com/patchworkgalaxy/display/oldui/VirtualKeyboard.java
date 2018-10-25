package com.patchworkgalaxy.display.oldui;

import static com.jme3.input.KeyInput.*;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;

public class VirtualKeyboard implements RawInputListener {
    
    private final UX2D _ui;
    
    private final boolean[] _keys = new boolean[256];
    
    private boolean _capslock = false;
    private boolean _shift, _ctrl, _alt;
    
    private char _recentChar;
    
    public static final char BACKSPACE = (char)8;
    public static final char TAB = (char)9;
    public static final char NEWLINE = (char)10;
    public static final char ESC = (char)27;
    
    VirtualKeyboard(UX2D ui) {
	_ui = ui;
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
	int keycode = evt.getKeyCode();
	if(keycode >= 128)
	    return;
	boolean value = evt.isPressed();
	_keys[keycode] = value;
	
	if(!value && keycode == KEY_CAPITAL)
	    _capslock = !_capslock;
	if(keycode == KEY_LSHIFT || keycode == KEY_RSHIFT)
	    _shift = _keys[KEY_LSHIFT] || _keys[KEY_RSHIFT];
	if(keycode == KEY_LCONTROL || keycode == KEY_RCONTROL)
	    _ctrl = _keys[KEY_LCONTROL] || _keys[KEY_RCONTROL];
	if(keycode == KEY_LMENU || keycode == KEY_RMENU)
	    _alt = _keys[KEY_LMENU] || _keys[KEY_RMENU];
	
	char c = evt.getKeyChar();
	if(c > 0) {
	    if(value) {
		if(_capslock ^ _shift)
		    c = Character.toUpperCase(c);
		_recentChar = c;
	    }
	    else
		_recentChar = 0;
	}
		
	if(value) {
	    boolean consume = true;
	    if(keycode == KEY_BACK)
		_ui.acceptKeyInput(BACKSPACE);
	    else if(_ctrl && keycode == 47) //V
		_ui.paste();
	    else if(keycode == KEY_TAB)
		_ui.acceptKeyInput(TAB);
	    else if(keycode == KEY_RETURN || keycode == KEY_NUMPADENTER)
		_ui.acceptKeyInput(NEWLINE);
	    else if(keycode == KEY_ESCAPE)
		_ui.acceptKeyInput(ESC);
	    else if(c >= 32)
		_ui.acceptKeyInput(_recentChar);
	    else
		consume = false;
	    if(consume)
		evt.setConsumed();
	}
    }

    public char getRecentChar() {
	return _recentChar;
    }
    
    /**
     * Checks the state of a key.
     * <p>
     * Use the keycode constants defined in {@link com.jme3.input.KeyInput} to
     * indicate which key.
     * </p>
     * @param keycode the keycode to check
     * @return true if pressed, false if not
     */
    public boolean getKey(int keycode) {
	return _keys[keycode];
    }
    
    /**
     * Checks the state of a key, then sets it to false.
     * <p>
     * This is often used when a function wants to be called as long as a key is
     * held down, but only as fast as keyboard input is usually checked instead
     * of every frame. Its checked when backspacing through text nodes.
     * </p>
     * <p>
     * Use the keycode constants defined in {@link com.jme3.input.KeyInput} to
     * indicate which key.
     * </p>
     * @param keycode the keycode to check
     * @return true if pressed, false if not
     */
    public boolean consumeKey(int keycode) {
	boolean result = _keys[keycode];
	_keys[keycode] = false;
	return result;
    }
    
    public boolean getShift() {
	return _shift;
    }
    
    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
	_ui.setMousePos(new Vector2f(evt.getX(), evt.getY()));
    }
    
    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
	if(evt.isReleased()) {
	    if(evt.getButtonIndex() == MouseInput.BUTTON_LEFT)
		_ui.acceptClickInput(new Vector2f(evt.getX(), evt.getY()));
	    else if(evt.getButtonIndex() == MouseInput.BUTTON_RIGHT)
		_ui.acceptKeyInput(ESC);
	}
	
    }
	
    //unused stuff begins here...
    @Override
    public void beginInput() { }
    @Override
    public void endInput() { }
    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) { }
    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {}
    @Override
    public void onTouchEvent(TouchEvent evt) { }
   
}
