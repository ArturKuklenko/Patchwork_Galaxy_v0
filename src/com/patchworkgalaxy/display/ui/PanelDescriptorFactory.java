package com.patchworkgalaxy.display.ui;

import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;
import com.patchworkgalaxy.display.ui.defs.game.PatchSpacePanelDescriptor;

public class PanelDescriptorFactory {
    private PanelDescriptorFactory() {}
    
    public static PanelDescriptor getPanelDescriptor(String... args) {
	return new PatchSpacePanelDescriptor();
    }
    
    public static void showPanelDescriptor(String... args) {
	UI.Instance.showPanel(getPanelDescriptor(args));
    }    
    
}
