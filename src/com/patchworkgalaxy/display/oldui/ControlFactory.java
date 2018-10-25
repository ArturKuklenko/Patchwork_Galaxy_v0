package com.patchworkgalaxy.display.oldui;

public interface ControlFactory {
    
    UX2DControl getControl(UX2D ux2d, TextInput.Factory input, ControlState defaultState, String key);
    
}
