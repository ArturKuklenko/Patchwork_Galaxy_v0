package com.patchworkgalaxy.display.oldui;

import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.general.subscriptions.Topic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public final class UX2D {
    
    private static UX2D _instance;
    
    private static PatchworkGalaxy _app;
    
    private final Map<String, UX2DControl> _controls;
    private final Map<String, List<String>> _sections;
    private List<String> _creatingSection;
    private String _sectionName;
    private final List<UX2DControl> _controlsByZIndex;
    private boolean _zIndexingInvalid;
    
    private final Node _guiNode;
    private final Vector2f _mousePos, _updatePos; //not really final, JMonkey vectors are mutable
    private char _recentKeypress;
    private final VirtualKeyboard _vkeyboard;
    private boolean _waitingClick, _waitingKeypress, _waitingMouseMove;
    private float _tooltipTimer;
    
    private UX2DControl _recentControl;
    private Object _tooltipAnchor;
    
    final Observer UPDATE_Z_INDEXING = new Observer() {
	@Override
	public void update(Observable o, Object arg) {
	    _instance._zIndexingInvalid = true;
	}
    };
    
    private UX2D() {
	_controls = new HashMap<>();
	_sections = new HashMap<>();
	_controlsByZIndex = new ArrayList<>();
	_guiNode = new Node("UX2D");
	_mousePos = new Vector2f();
	_updatePos = new Vector2f();
	_vkeyboard = new VirtualKeyboard(this);
	_guiNode.addControl(new AbstractControl(){
	    @Override protected void controlRender(RenderManager rm, ViewPort vp) {}

	    @Override
	    protected void controlUpdate(float tpf) {
		_instance.update(tpf);
	    }
	});
    }
    
    public static UX2D getInstance() {
	if(_instance == null) {
	    _instance = new UX2D();
	    PatchworkGalaxy.inputManager().addRawInputListener(_instance._vkeyboard);
	    PatchworkGalaxy.guiNode().attachChild(_instance._guiNode);
	}
	return _instance;
    }
    
    private void update(float tpf) {
	if(_waitingClick) {
	    _waitingClick = false;
	    handleMouseMovement();
	    handleClick();
	}
	else if(_waitingKeypress) {
	    _waitingKeypress = false;
	    handleMouseMovement();
	    handleKeypress();
	}
	else
	    handleMouseMovement();
	checkTooltip(tpf);
    }
    
    void updateZIndexing() {
	if(!_zIndexingInvalid)
	    return;
	Collections.sort(_controlsByZIndex);
	_zIndexingInvalid = false;
    }
    
    void tabFocus(UX2DControl from) {
	UX2DControl focus = getNextFocusControl(from);
	if(focus != null)
	    focus.focus();
    }
    
    UX2DControl getNextFocusControl(UX2DControl control) {
	updateZIndexing();
	UX2DControl first = null;
	boolean found = false;
	for(UX2DControl consider : _controlsByZIndex) {
	    if(!consider.isFocusable())
		continue;
	    if(first == null)
		first = consider;
	    if(found)
		return consider;
	    if(control == consider)
		found = true;
	}
	return first;
    }
    
    void handleClick() {
	updateZIndexing();
	for(UX2DControl control : new ArrayList<>(_controlsByZIndex)) {
	    if(control.click(_mousePos)) {
		Topic.UI_CONTROL_CLICKED.update(control);
		return;
	    }
	}
	UX2DControl.blurFocusedControl();
	//PatchworkGalaxy.passInputToGameState(_mousePos);
	killControl("Tooltip");
	Topic.UI_CONTROL_CLICKED.update();
    }
    
    void paste() {
	if(UX2DControl._focus != null)
	    UX2DControl._focus.paste();
    }
    
    void handleKeypress() {
	killControl("Tooltip");
	if(UX2DControl._focus != null) {
	    UX2DControl._focus.keypress(_recentKeypress);
	}
	else {
	    updateZIndexing();
	    for(UX2DControl control : new ArrayList<>(_controlsByZIndex)) {
		if(control.keypress(_recentKeypress)) {
		    return;
		}
	    }
	}
	Topic.UI_KEY_PRESSED.update(_recentKeypress);
    }
    
    private void handleMouseMovement() {
	if(!_waitingMouseMove)
	    return;
	
	updateZIndexing();
	for(UX2DControl control : new ArrayList<>(_controlsByZIndex)) {
	    boolean wasInside = control.checkMousePos(_mousePos);
	    boolean isInside = control.checkMousePos(_updatePos);
	    //TODO: there are situations in which a component's mouse out event
	    //should trigger even if we weren't in that component last frame,
	    //eg. if the component has a mouse in event that moves or reshapes it
	    //we should trigger those
	    if(wasInside && !isInside)
		control.doCallback(CallbackType.MOUSE_OUT);
	    //TODO: we should only trigger mouse in events on one component,
	    //ie. higher components should occlude lower ones,
	    //as they do for click events
	    if(isInside && !wasInside)
		control.doCallback(CallbackType.MOUSE_IN);
	}
	_mousePos.set(_updatePos);
	_waitingMouseMove = false;
    }
    
    private void checkTooltip(float tpf) {
	boolean dostuff = false;
	if(_tooltipTimer <= 0) {
	    _tooltipTimer += tpf;
	    if(_tooltipTimer > 0)
		dostuff = true;
	}
	if(!dostuff)
	    return;
	
	UX2DControl tooltipAnchor = null;
	for(UX2DControl control : new ArrayList<>(_controlsByZIndex)) {
	    boolean wasInside = control.checkMousePos(_mousePos);
	    boolean isInside = control.checkMousePos(_updatePos);
	    if(wasInside && !isInside)
		control.doCallback(CallbackType.MOUSE_OUT);
	    if(isInside && !wasInside)
		control.doCallback(CallbackType.MOUSE_IN);
	    if(tooltipAnchor == null && isInside)
		tooltipAnchor = control;
	}
	updateTooltip(tooltipAnchor);
    }
    
    public void checkMousePos() {
	updateZIndexing();
	for(UX2DControl control : new ArrayList<>(_controlsByZIndex)) {
	    if(control.checkMousePos(_updatePos))
		control.doCallback(CallbackType.MOUSE_IN);
	}
    }
    
    public void beginSection(String sectionName) {
	_sectionName = sectionName;
	_creatingSection = new ArrayList<>();
    }
    
    public void concludeSection() {
	if(_sections.containsKey(_sectionName))
	    _sections.get(_sectionName).addAll(_creatingSection);
	else
	    _sections.put(_sectionName, _creatingSection);
	_creatingSection = null;
    }
    
    public void killSection(String sectionName) {
	List<String> section = _sections.remove(sectionName);
	if(section != null) {
	    for(String key : section)
		killControl(key);
	}
    }
    
    private void updateTooltip(ColoredText text, float width) {
	UX2DControl tooltip = createControl(
	    "Tooltip",
	    new ControlState()
	        .setBackground("Interface/offblack.png")
	        .setZIndex(99999)
	        .setText(text)
		.setOpacity(.85f)
	);
	tooltip.controlUpdate(0);
	tooltip.updateAsTooltip(width);
    }
    
    public void updateTooltip(UX2DControl control) {
	if(control == _tooltipAnchor)
	    return;
	if(control == null) {
	    killControl("Tooltip");
	    return;
	}
	if(control.isTooltip())
	    return;
	ColoredText text = control.getTooltip();
	if(text == null || text.toString().isEmpty())
	    killControl("Tooltip");
	else {
	    float width = control.getTooltipWidth();
	    if(!(width > 0))
		width = 200;
	    updateTooltip(text, width);
	}
	setTooltipAnchor(control);
    }
    
    public void updateTooltip(ColoredText text, Object anchor) {
	updateTooltip(text, anchor, 200);
    }
    
    public void updateTooltip(ColoredText text, Object anchor, float width) {
	if(anchor.equals(_tooltipAnchor))
	    return;
	setTooltipAnchor(anchor);
	updateTooltip(text, width);	
    }
    
    public UX2DControl createControl(String key, TextInput.Factory input, ControlState defaultState) {
	if(_controls.get(key) != null)
	    killControl(key);  
	UX2DControl control = new UX2DControl(this, input, defaultState, key);
	_controls.put(key, control);
	_controlsByZIndex.add(control);
	_zIndexingInvalid = true;
	_guiNode.attachChild(control.getNode());
	_recentControl = control;
	if(_creatingSection != null)
	    _creatingSection.add(key);
	return control;
    }
    
    public UX2DControl createControl(ControlFactory factory, String key, TextInput.Factory input, ControlState defaultState) {
	if(_controls.get(key) != null)
	    killControl(key);  
	UX2DControl control = factory.getControl(this, input, defaultState, key);
	_controls.put(key, control);
	_controlsByZIndex.add(control);
	_zIndexingInvalid = true;
	_guiNode.attachChild(control.getNode());
	_recentControl = control;
	if(_creatingSection != null)
	    _creatingSection.add(key);
	return control;
    }
    
    public UX2DControl createControl(String key, ControlState defaultState) {
	return createControl(key, null, defaultState);
    }
    
    public UX2DControl recentControl() {
	return _recentControl;
    }
    
    public void delayUpdateControl(String key, ControlState state, float duration) {
	_controls.get(key).delayStateChange(state, duration);
    }
    
    public void flashControl(String key) {
	_controls.get(key).flash();
    }
    
    public void focusControl(String key) {
	UX2DControl control = _controls.get(key);
	if(control == null)
	    UX2DControl.blurFocusedControl();
	else
	    control.focus();
    }
    
    public void updateControl(String key, ControlState state) {
	_controls.get(key).changeState(state);
    }
    
    public void updateControlWithDuration(String key, ControlState state, float duration) {
	_controls.get(key).changeStateWithDuration(state, duration);
    }
    
    public void updateControlWithSpeed(String key, ControlState state, float speed) {
	_controls.get(key).changeStateWithSpeed(state, speed);
    }
    
    public void setControlText(String key, String text) {
	_controls.get(key).setText(text);	
    }
    
    public void setControlText(String key, ColoredText text) {
	UX2DControl control = _controls.get(key);
	if(control != null)
	    control.setText(text);
    }
    
    public String readControl(String key) {
	UX2DControl control = _controls.get(key);
	if(control != null)
	    return control.getInputText();
	return "";
    }
    
    public void killControl(String key) {
	UX2DControl control = _controls.remove(key);
	if(control != null) {
	    control.setDead();
	    _controlsByZIndex.remove(control);
	}
	if(key.equals("Tooltip")) {
	    setTooltipAnchor(null);
	}
    }
    
    private void setTooltipAnchor(Object anchor) {
	_tooltipAnchor = anchor;
    }
    
    public void killControls(String... keys) {
	for(String key : keys)
	    killControl(key);
    }
    
    public void killAllControls() {
	for(String key : new HashSet<>(_controls.keySet()))
	    killControl(key);
    }
    
    public UX2DControl getControl(String key) {
	return _controls.get(key);
    }
    
    public Vector2f getMousePos() {
	return new Vector2f(_updatePos);
    }
    
    void setMousePos(Vector2f mousePos) {
	_waitingMouseMove = true;
	_tooltipTimer = -.05f;
	_updatePos.set(mousePos);
    }
    
    public void acceptClickInput(Vector2f mousePos) {
	_mousePos.set(mousePos);
	_updatePos.set(mousePos);
	_waitingClick = true;
    }
    
    public void acceptKeyInput(char keypress) {
	_recentKeypress = keypress;
	_waitingKeypress = true;
    }
    
    public void acceptChatInput(ColoredText chatInput) {
	UX2DControl.acceptChatInput(chatInput);
    }
    
}