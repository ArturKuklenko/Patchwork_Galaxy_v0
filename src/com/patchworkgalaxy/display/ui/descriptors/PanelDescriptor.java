package com.patchworkgalaxy.display.ui.descriptors;

import com.patchworkgalaxy.display.ui.controller.Panel;
import java.util.Set;

public interface PanelDescriptor {
    
    Set<ComponentDescriptor> getComponentDescriptors();
    
    void onShow(Panel panel);
    
    void onHide(Panel panel);
    
}
