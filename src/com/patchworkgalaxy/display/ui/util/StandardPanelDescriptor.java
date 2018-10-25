package com.patchworkgalaxy.display.ui.util;

import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class StandardPanelDescriptor implements PanelDescriptor {
    
    private final Set<ComponentDescriptor> _components;
    
    public StandardPanelDescriptor(Collection<ComponentDescriptor> components) {
	_components = new HashSet<>(components);
    }
    
    public StandardPanelDescriptor(ComponentDescriptor... components) {
	_components = new HashSet<>(Arrays.asList(components));
    }

    @Override public Set<ComponentDescriptor> getComponentDescriptors() {
	return new HashSet<>(_components);
    }
    
    protected void setComponents(Collection<? extends ComponentDescriptor> components) {
	_components.clear();
	_components.addAll(components);
    }

    @Override public void onShow(Panel panel) {}
    
    @Override public void onHide(Panel panel) {}
    
}
