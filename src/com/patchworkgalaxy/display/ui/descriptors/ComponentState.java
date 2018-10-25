package com.patchworkgalaxy.display.ui.descriptors;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;

public interface ComponentState {
    
    Vector2f getCenter();
    
    Vector2f getSize();
    
    String getBackgroundImage();
    
    float getOpacity();
    
    float getZIndex();
    
    ColoredText getText();
    
    float getTextSize();
    
}
