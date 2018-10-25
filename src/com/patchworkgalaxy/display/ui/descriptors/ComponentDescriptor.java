package com.patchworkgalaxy.display.ui.descriptors;

import com.jme3.font.BitmapFont;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ComponentDescriptor extends ComponentState {
    
    String getName();
    
    TextInputDescriptor getTextInputDescriptor();
    
    BitmapFont.Align getTextAlignment();
    
    BitmapFont.VAlign getTextVAlignment();
    
    TooltipDescriptor getTooltipDescriptor();
    
    Map<Property.Type, Float> getTransitionDurations();
    
    Set<ComponentCallback> getCallbacks();
    
    void onShow(Component component);
    
    void onHide(Component component);
    
    char getHotkey();
    
    List<Subscribable> getSubscribeTo();
    
}
