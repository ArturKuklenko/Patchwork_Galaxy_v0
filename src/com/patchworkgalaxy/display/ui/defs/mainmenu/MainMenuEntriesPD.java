package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class MainMenuEntriesPD extends StandardPanelDescriptor {
    
    private static final int[] COUNT = new int[] { 3, 2, 3, 3 };
    
    private static Collection<ComponentDescriptor> getComponents(int index) {
	int len = COUNT[index];
	List<ComponentDescriptor> result = new ArrayList<>();
	for(int i = 0; i < len; ++i)
	    result.add(new MainMenuEntryCD(index, i));
	return result;
    }
    
    MainMenuEntriesPD(int index) {
	super(getComponents(index));
    }
    
}
