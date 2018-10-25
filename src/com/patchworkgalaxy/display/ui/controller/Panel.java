package com.patchworkgalaxy.display.ui.controller;

import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Panel {
    
    private final Map<String, Component> _components;
    private final String _id;
    private final PanelDescriptor _descriptor;
    
    public Panel(String id, PanelDescriptor descriptor, Set<Component> components) {
	_id = id;
	_components = new HashMap<>();
	for(Component component : components) {
	    component.setPanel(this);
	    String componentId = component.getId();
	    if(componentId != null)
		_components.put(componentId, component);
	}
	_descriptor = descriptor;
    }
    
    public void hide() {
	_descriptor.onHide(this);
	for(Component component : _components.values())
	    component.getDescriptor().onHide(component);
	UI.Instance.hidePanel(this);
    }
    
    public Set<Component> getComponents() {
	return new HashSet<>(_components.values());
    }
    
    public Component getComponent(String key) {
	return _components.get(_id + key);
    }
    
    public String readComponent(String key) {
	Component component = getComponent(key);
	if(component == null) return "";
	String result = component.getInputText();
	if(result == null) return "";
	return result;
    }
    
    public String getId() {
	return _id;
    }
    
}