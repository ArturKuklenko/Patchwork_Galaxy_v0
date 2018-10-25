package com.patchworkgalaxy.display.oldui;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.ui.Picture;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.oldui.ux.ChannelMutex;
import com.patchworkgalaxy.display.oldui.ux.StandardUX;
import com.patchworkgalaxy.display.oldui.ux.UXChannel;
import com.patchworkgalaxy.display.oldui.ux.UXMutex;
import com.patchworkgalaxy.display.oldui.ux.WriteRequest;
import com.patchworkgalaxy.display.oldui.ux.WriteRequest.Priority;
import java.util.EnumMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class UX2DControl extends AbstractControl implements Observer, Comparable<UX2DControl> {

    private boolean _recentUpdate, _observedUpdate;
    private final UX2D _ux2d;
    private final StandardUX _ux;
    private ChannelMutex _textMutex;
    private UXMutex _mutex;
    private final ControlState _defaultState;
    
    private final TextInput _textInput;
    private final Map<CallbackType, UICallback> _callbacks;
    private Picture _backgroundImage;
    private BitmapText _bitmapText;
    private final ColorRGBA _alpha;
    
    private final UXChannel<Vector2f> _center, _dimensions, _offset;
    private final UXChannel<Float> _zIndex, _opacity, _fontSize, _tooltipWidth;
    private final UXChannel<ColoredText> _text, _tooltipText;
    private final UXChannel<String> _background;
    private final UXChannel<BitmapFont.Align> _textAlign;
    private final UXChannel<BitmapFont.VAlign> _textVAlign;
    private final UXChannel<Character> _hotkey;
    private final UXChannel<Boolean> _valid;
    
    /*private*/ final String _name;
    private boolean _dead, _initialized;
    private float _tpf;
    
    private Node _node;
    
    static UX2DControl _focus;
    private static ChatLog _chatLog;
    
    protected UX2DControl(UX2D ux2d, TextInput.Factory input, ControlState defaultState, String key) {
	_ux2d = ux2d;
	_name = key;
	_ux = new StandardUX();
	_center = initObservedChannel(Vector2f.class, "center");
	_dimensions = initObservedChannel(Vector2f.class, "dimensions");
	_offset = initObservedChannel(Vector2f.class, "offset");
	_zIndex = initObservedChannel(Float.class, "z-index");
	_zIndex.addObserver(ux2d.UPDATE_Z_INDEXING);
	_text = initObservedChannel(ColoredText.class, "text");
	_tooltipText = initObservedChannel(ColoredText.class, "tooltip");
	_tooltipWidth = initObservedChannel(Float.class, "tooltip-width");
	_textMutex = _text.acquireMutex();
	_opacity = initObservedChannel(Float.class, "opacity");
	_fontSize = initObservedChannel(Float.class, "font-size");
	_background = initObservedChannel(String.class, "background");
	_textAlign = initObservedChannel(BitmapFont.Align.class, "text-alignment");
	_textVAlign = initObservedChannel(BitmapFont.VAlign.class, "text-vertical-alignment");
	_hotkey = initObservedChannel(Character.class, "hotkey");
	_valid = initObservedChannel(Boolean.class, "valid");
	_mutex = _ux.acquireMutex();
	_alpha = new ColorRGBA(ColorRGBA.White);
	_defaultState = defaultState;
	TextInput textInput;
	_callbacks = new EnumMap<>(CallbackType.class);
	if(input == null)
	    textInput = null;
	else
	    textInput = input.create(this); //this is safe
	_textInput = textInput;
	_node =  new Node("UX Node");
	changeState(defaultState);
	_node.addControl(this); //this is safe
    }
    
    private <T> UXChannel<T> initObservedChannel(Class<T> type, String key) {
	UXChannel<T> channel = _ux.getChannel(type, key);
	channel.addObserver(this);
	return channel;
    }
    
    ChannelMutex acquireTextMutex() {
	return _text.acquireMutex();
    }
    
    public ControlState getDefaultState() {
	return new ControlState(_defaultState);
    }
    
    public void changeState(ControlState state) {
	applyState(state, 0, false);
    }
    
    public void changeStateWithDuration(ControlState state, float duration) {
	applyState(state, duration, false);
    }
    
    public void changeStateWithSpeed(ControlState state, float speed) {
	applyState(state, speed, true);
    }
    
    public void delayStateChange(ControlState state, float duration) {
	applyState(state, duration, false, true);
    }
    
    public void flash() {
	_ux.releaseMutex(_mutex);
	_mutex = _ux.acquireMutex();
    }
    
    private void applyState(ControlState state, float durationOrSpeed, boolean speed) {
	applyState(state, durationOrSpeed, speed, false);
    }
    
    private void applyState(ControlState state, float durationOrSpeed, boolean speed, boolean delayed) {
	if(state == null)
	    state = _defaultState;
	
	if(_mutex == null)
	    _mutex = _ux.acquireMutex();
	
	applyStateHelper(_center, state._center, durationOrSpeed, speed, delayed);
	applyStateHelper(_dimensions, state._dimensions, durationOrSpeed, speed, delayed);
	applyStateHelper(_offset, state._offset, durationOrSpeed, speed, delayed);
	applyStateHelper(_zIndex, state._zIndex, durationOrSpeed, speed, delayed);
	applyStateHelper(_opacity, state._opacity, durationOrSpeed, speed, delayed);
	applyStateHelper(_fontSize, state._fontSize, durationOrSpeed, speed, delayed);
	applyStateHelper(_tooltipText, state._tooltipText, durationOrSpeed, speed, delayed);
	applyStateHelper(_tooltipWidth, state._tooltipWidth, durationOrSpeed, speed, delayed);
	applyStateHelper(_background, state._background, durationOrSpeed, speed, delayed);
	applyStateHelper(_hotkey, state._hotkey, durationOrSpeed, speed, delayed);
	applyStateHelper(_valid, state._valid, durationOrSpeed, speed, delayed);
	if(state._textAlign != null)
	    applyStateHelper(_textAlign, state._textAlign, durationOrSpeed, speed, delayed);
	if(state._textVAlign != null)
	    applyStateHelper(_textVAlign, state._textVAlign, durationOrSpeed, speed, delayed);
	if(state._text != null)
	    setText(state._text);
    }
    
    private <T> void applyStateHelper(UXChannel<T> channel, T value, float durationOrSpeed, boolean speed, boolean delayed) {
	if(value == null)
	    return;
	WriteRequest<T> wr;
	if(speed)
	    wr = channel.getWriteRequestWithSpeed(value, durationOrSpeed);
	else
	    wr = channel.getWriteRequestWithDuration(value, durationOrSpeed);
	channel.write(wr, delayed ? null : _mutex, Priority.HIGH);
    }
    
    StandardUX getUX() {
	return _ux;
    }
    
    UX2D getUX2D() {
	return _ux2d;
    }
    
    Node getNode() {
	return _node;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
	if(_dead) {
	    _node.removeFromParent();
	}
	else if(!_initialized) {
	    doCallback(CallbackType.INITIALIZE);
	    _initialized = true;
	}
	doCallback(CallbackType.TICK);
	if(_observedUpdate == true) {
	    doCallback(CallbackType.OBSERVE);
	    _observedUpdate = false;
	}
	_ux.update(tpf);
	if(_recentUpdate) {
	    if(_valid.getValue())
		updateSpatialFromUX();
	    else
		_ux2d.killControl(_name);
	    _recentUpdate = false;
	}
	_tpf = tpf;
	if(_textInput != null)
	    _textInput.update(tpf);
    }
    
    public UX2DControl setCallback(CallbackType type, UICallback callback) {
	_callbacks.put(type, callback);
	return this;
    }
    
    public UICallback getCallback(CallbackType type) {
	return _callbacks.get(type);
    }
    
    public UX2DControl setText(ColoredText text) {
	WriteRequest<ColoredText> wr = WriteRequest.immediate(text);
	_text.write(wr, _textMutex);
	return this;
    }
    
    public UX2DControl setText(String text) {
	ColoredText ct = new ColoredText(text);
	WriteRequest<ColoredText> wr = WriteRequest.immediate(ct);
	_text.write(wr, _textMutex);
	return this;
    }
    
    public UX2DControl pipeTextToInput(String text) {
	if(text == null)
	    text = "";
	if(_textInput != null)
	    _textInput.setText(text);
	return this;
    }
    
    public UX2DControl clearInput() {
	if(_textInput != null)
	    _textInput.setText("");
	return this;
    }
    
    public <T> UXChannel<T> manualChannelControl(Class<T> type, String key) {
	UXChannel<T> channel = _ux.getChannel(type, key);
	channel.releaseMutex(_mutex);
	return channel;
    }
    
    boolean isValid() {
	return _valid.getValue();
    }

    @Override
    public void update(Observable o, Object arg) {
	if(arg == UXChannel.class)
	    _recentUpdate = true;
	else
	    _observedUpdate = true;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private void updateSpatialFromUX() {
	Vector2f absoluteCenter = getAbsoluteCenter();
	Vector2f absoluteDimensions = getAbsoluteDimensions();
	Vector2f offset = absoluteDimensions.mult(.5f);
	Vector2f leftCorner = absoluteCenter.subtract(offset);
	Vector2f rightCorner = absoluteCenter.add(offset);
	
	spatial.setLocalTranslation(leftCorner.x, leftCorner.y, _zIndex.getValue());
	
	float width = rightCorner.x - leftCorner.x;
	float height = rightCorner.y - leftCorner.y;
	updateText(width, height);
	updateBackgroundImage(width, height);
    }
    
    private Vector2f getAbsoluteCenter() {
	Vector2f abs = PatchworkGalaxy.getDimensions();
	Vector2f rel = _center.getValue();
	abs.x *= ((rel.x + 1)/2);
	abs.y *= ((rel.y + 1)/2);
	return abs;
    }
    
    private Vector2f getAbsoluteDimensions() {
	Vector2f abs = PatchworkGalaxy.getDimensions();
	Vector2f rel = _dimensions.getValue();
	abs.x *= rel.x;
	abs.y *= rel.y;
	return abs;
    }
    
    private Vector2f getAbsoluteOffset() {
	Vector2f abs = PatchworkGalaxy.getDimensions();
	Vector2f rel = _offset.getValue();
	abs.x *= rel.x;
	abs.y *= rel.y;
	return abs;	
    }
    
    public Vector2f getMousePos() {
	return _ux2d.getMousePos();
    }
    
    public String getInputText() {
	if(_textInput == null)
	    return "";
	else
	    return _textInput.getText();
    }
    
    public float heightFromText(float width, float height) {
	_bitmapText.setBox(new Rectangle(0, height, width, 0));
	height = _bitmapText.getLineCount() * _bitmapText.getLineHeight();
	_bitmapText.setBox(new Rectangle(0, height, width, 0));
	return height;
    }
    
    void updateAsTooltip(float width) {
	float height = heightFromText(width, 1);
	_backgroundImage.setWidth(width);
	_backgroundImage.setHeight(height);
	_backgroundImage.setLocalTranslation(new Vector3f(0, 0, -1));
	
	Vector2f anchor = getMousePos();
	anchor.x += 10; //offset the tooltip so the mouse doesn't get in the way
	anchor.y -= height;
	if(anchor.y < 0)
	    anchor.y = 0;
	
	Vector2f windowSize = PatchworkGalaxy.getDimensions();
	Vector2f right = anchor.add(new Vector2f(width, height));
	if(right.x > windowSize.x) {
	    float delta = right.x - windowSize.x;
	    anchor.x -= delta;
	    anchor.y -= 10; //we need to offset the tooltip again
	}
	if(anchor.y < 0)
	    anchor.y = 0;
	
	spatial.setLocalTranslation(new Vector3f(anchor.x, anchor.y, _zIndex.getValue()));
    }
    
    private void createBackgroundImage(String imagePath) {
	
	_backgroundImage = new Picture("Background image");
	_backgroundImage.setImage(PatchworkGalaxy.assetManager(), imagePath, true);
	
	Material alphaMaterial = _backgroundImage.getMaterial().clone();
	alphaMaterial.setColor("Color", _alpha);
	alphaMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
	_backgroundImage.setMaterial(alphaMaterial);
	
	_node.attachChild(_backgroundImage);
	
    }
    
    private void updateBackgroundImage(float width, float height) {
	
	String imagePath = _background.getValue();
	if(imagePath != null && imagePath.length() == 0)
	    imagePath = null;
	
	if(imagePath == null) {
	    if(_backgroundImage != null) {
		_backgroundImage.removeFromParent();
		_backgroundImage = null;
	    }
	    return;
	}
	
	if(_backgroundImage == null)
	    createBackgroundImage(imagePath);
	else
	    _backgroundImage.setImage(PatchworkGalaxy.assetManager(), imagePath, true);
	
	
	_backgroundImage.setWidth(width);
	_backgroundImage.setHeight(height);
	_backgroundImage.setLocalTranslation(new Vector3f(0, 0, -1));
	    
	float opacity = _opacity.getValue();
	if(opacity < 0)
	    opacity = 0;
	if(opacity > 1)
	    opacity = 1;
	_alpha.a = opacity;
    }
    
    private void updateText(float width, float height) {
	if(_bitmapText == null) {
	    _bitmapText = new BitmapText(getFont());
	    _node.attachChild(_bitmapText);
	}
	ColoredText ctext = _text.getValue();
	if(ctext != null) {
	    ctext.setBitmapTextTo(_bitmapText);
	    Vector2f offset = getAbsoluteOffset();
	    updateFontSize();
	    _bitmapText.setBox(new Rectangle(offset.x, offset.y + height, width, height/2));
	    if(_textAlign.getValue() != null)
		_bitmapText.setAlignment(_textAlign.getValue());
	    if(_textVAlign.getValue() != null)
		_bitmapText.setVerticalAlignment(_textVAlign.getValue());
	    _bitmapText.setAlpha(_opacity.getValue());
	}
	else {
	    _bitmapText.removeFromParent();
	    _bitmapText = null;
	}
    }
    
    private BitmapFont getFont() {
	return PatchworkGalaxy.getGuiFont();
    }
    
    private float updateFontSize() {
	float result = (float)_fontSize.getValue() * PatchworkGalaxy.getAppResolutionScale();
	if(_bitmapText != null)
	    _bitmapText.setSize(result);
	return result;
    }
    
    protected boolean checkMousePos(Vector2f mousePos) {
	
	if(isTransparent())
	    return false;
	
	//find the click position in local space...
	Vector2f offset = getAbsoluteCenter().subtract(mousePos);
	
	//we're going to take the absolute value of each coordinate
	//of that position and double it
	//if those coordinates are both less than the corresponding value
	//in our dimension vector, then we've clicked this button

	//eg. if our local mouse position is (-47, 58), that yields (94, 116)
	//if our dimensions are (100, 80), our horizontal dimension is fine
	//but our vertical dimension isn't, so we've clicked outside
	//(94 < 100, but 116 > 80)
	
	offset.x = Math.abs(offset.x) * 2;
	offset.y = Math.abs(offset.y) * 2;
	
	Vector2f dim = getAbsoluteDimensions();
	
	return offset.x < dim.x && offset.y < dim.y;
    }
    
    boolean checkHotkey(char hotkey) {
	if(isTransparent())
	    return false;
	Character checkAgainst = _hotkey.getValue();
	if(checkAgainst == null)
	    return false;
	return Character.toLowerCase(checkAgainst) == hotkey;
    }

    @Override
    public int compareTo(UX2DControl o) {
	return (int)(o._zIndex.getValue() - _zIndex.getValue());
    }
    
    public void focus() {
	if(_textInput != null) {
	    blurFocusedControl();
	    _focus = this;
	    _textInput.update(0);
	}
    }
    
    public void blur() {
	if(_focus == this) {
	    _focus = null;
	    if(_textInput != null)
		_textInput.onBlur();
	}
	    
    }
    
    public static void blurFocusedControl() {
	if(_focus != null) {
	    _focus.blur();
	}
    }
    
    public boolean hasFocus() {
	return _focus == this;
    }
    
    public boolean isFocusable() {
	return isValid() && isEnabled() && !isTransparent() && _textInput != null;
    }
    
    boolean isTransparent() {
	float zIndex = _zIndex.getValue();
	return (zIndex < 0 || zIndex > 5000 || _opacity.getValue() < .1f);
    }
    
    boolean click(Vector2f mousePos) {
	if(isTransparent())
	    return false;
	if(checkMousePos(mousePos)) {
	    if(_textInput != null)
		focus();
	    doCallback(CallbackType.CLICK);
	    return true;
	}
	else
	    return false;
    }
    
    void paste() {
	if(hasFocus())
	    _textInput.paste();
    }
    
    boolean keypress(char keypress) {
	if(hasFocus()) {
	    _textInput.type(keypress);
	    return true;
	}
	else if(checkHotkey(keypress)) {
	    doCallback(CallbackType.CLICK);
	    if(_textInput != null)
		focus();
	    return true;
	}
	else
	    return false;
    }
    
    boolean doCallback(CallbackType callbackType) {
	UICallback callback = _callbacks.get(callbackType);
	if(callback != null) {
	    callback.callback(this);
	    return true;
	}
	return false;
    }
    
    public void setDead() {
	if(!_dead) {
	    doCallback(CallbackType.TEARDOWN);
	    blur();
	    _dead = true;
	}
    }
    
    public UX2DControl attachChatLog(int lines) {
	attachChatLog(lines, Float.NaN);
	return this;
    }
    
    public UX2DControl attachChatLog(int lines, float messageDuration) {
	Vector2f bottomCenter = new Vector2f(_center.getValue());
	Vector2f dim = _dimensions.getValue();
	bottomCenter.y -= dim.y;
	_chatLog = new ChatLog(_name, lines, bottomCenter, dim, messageDuration);
	_node.addControl(_chatLog);
	return this;
    }
    
    public float getTPF() {
	return _tpf;
    }
    
    static void acceptChatInput(ColoredText chatInput) {
	if(_chatLog == null)
	    return;
	_chatLog.addText(chatInput);
    }
    
    public UX2DControl observe(Observable o) {
	o.addObserver(this);
	return this;
    }
    
    @Override
    public String toString() {
	return "Control " + _name;
    }
    
    public String getName() {
	return _name;
    }
    
    public ColoredText getTooltip() {
	return _tooltipText.getValue();
    }
    
    public float getTooltipWidth() {
	return _tooltipWidth.getValue();
    }
    
    boolean isTooltip() {
	return _name.equals("Tooltip");
    }
    
}
