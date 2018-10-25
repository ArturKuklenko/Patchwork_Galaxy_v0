package com.patchworkgalaxy.display.ui;

import com.patchworkgalaxy.display.oldui.ControlFactory;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.TextInput;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.TextInputDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.TooltipDescriptor;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum UI {
    Instance;
    
    private UX2D _oldui;
    private int _id;
    
    private final Map<String, Panel> _taggedPanels;
    private final Map<Panel, PanelDescriptor> _descriptors;
    
    private UI() {
	_oldui = UX2D.getInstance();
	_taggedPanels = new ConcurrentHashMap<>();
	_descriptors = new ConcurrentHashMap<>();
    }
    
    private String nextId() {
	return "" + ++_id;
    }
    
    public Panel showPanel(PanelDescriptor panelDescriptor) {
	String panelId = nextId();
	_oldui.beginSection(panelId);
	Set<Component> panelComponents = createComponents(panelId, panelDescriptor);
	_oldui.concludeSection();
	Panel panel = new Panel(panelId, panelDescriptor, panelComponents);
	_descriptors.put(panel, panelDescriptor);
	panelDescriptor.onShow(panel);
	return panel;	
    }
    
    public Panel showPanelWithTag(PanelDescriptor panelDescriptor, String tag) {
	if(tag == null) throw new NullPointerException();
	Panel clear = _taggedPanels.remove(tag);
	if(clear != null)
	    clear.hide();
	Panel panel;
	if(panelDescriptor != null) {
	    panel = showPanel(panelDescriptor);
	    _taggedPanels.put(tag, panel);
	}
	else
	    panel = null;
	return panel;
    }
    
    public void hidePanel(Panel panel) {
	if(_descriptors.containsKey(panel)) {
	    _descriptors.get(panel).onHide(panel);
	    _descriptors.remove(panel);
	}
	if(_taggedPanels.containsValue(panel))
	    _taggedPanels.values().remove(panel);
	_oldui.killSection(panel.getId());
    }
    
    public void hidePanelWithTag(String tag) {
	showPanelWithTag(null, tag);
    }
    
    public Panel getPanelWithTag(String tag) {
	return _taggedPanels.get(tag);
    }
    
    public void hideAllPanels() {
	for(Panel panel : _descriptors.keySet())
	    panel.hide();
    }
    
    private Set<Component> createComponents(String panelId, PanelDescriptor panelDescriptor) {
	Set<Component> components = new HashSet<>();
	for(ComponentDescriptor componentDescriptor : panelDescriptor.getComponentDescriptors()) {
	    Component component = createComponent(panelId, componentDescriptor);
	    components.add(component);
	}
	return components;
    }
    
    private Component createComponent(String panelId, ComponentDescriptor descriptor) {
	Component result = Component.create(descriptor, createControl(panelId, descriptor));
	descriptor.onShow(result);
	return result;
    }
    
    private UX2DControl createControl(String panelId, ComponentDescriptor descriptor) {
	String componentName = descriptor.getName();
	if(componentName == null)
	    componentName = nextId();
	componentName = panelId + componentName;
	UX2DControl oldControl;
	TextInputDescriptor inputDescriptor = descriptor.getTextInputDescriptor();
	if(descriptor instanceof ControlFactory) {
	    oldControl = _oldui.createControl(
		    (ControlFactory)descriptor,
		    componentName,
		    getOldStyleTextInputFactory(inputDescriptor),
		    getOldStyleState(descriptor)
		    );
	}
	else {
	    oldControl = _oldui.createControl(
		    componentName,
		    getOldStyleTextInputFactory(inputDescriptor),
		    getOldStyleState(descriptor)
		    );
	}
	if(inputDescriptor != null) {
	    String initialText = inputDescriptor.getInitialText();
	    if(initialText != null)
		oldControl.pipeTextToInput(initialText);
	}
	return oldControl;
    }
    
    private ControlState getOldStyleState(ComponentDescriptor descriptor) {
	ControlState result = new ControlState()
		.setBackground(descriptor.getBackgroundImage())
		.setCenter(descriptor.getCenter())
		.setDimensions(descriptor.getSize())
		.setFontSize(descriptor.getTextSize())
		.setHotkey(descriptor.getHotkey())
		.setOpacity(descriptor.getOpacity())
		.setText(descriptor.getText())
		.setTextAlign(descriptor.getTextAlignment())
		.setTextVAlign(descriptor.getTextVAlignment())
		.setZIndex(descriptor.getZIndex())
		;
	TooltipDescriptor tooltip = descriptor.getTooltipDescriptor();
	if(tooltip != null)
	    result.setTooltipText(tooltip.getTooltipText())
		    .setTooltipWidth(tooltip.getTooltipWidth());
	return result;
    }
    
    private TextInput.Factory getOldStyleTextInputFactory(TextInputDescriptor input) {
	if(input == null)
	    return null;
	return new TextInput.Factory(input.getPrefix(), input.getPrompt(), input.isEraseOnLostFocus(), input.isPassword());
    }
	
    
}
