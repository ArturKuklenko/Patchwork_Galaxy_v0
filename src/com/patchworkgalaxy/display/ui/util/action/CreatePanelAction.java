package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;

public class CreatePanelAction extends Action {
    
    private final PanelDescriptor _panelDescriptor;
    private final Panel[] _write;
    private final String _tag;
    
    public CreatePanelAction(PanelDescriptor panelDescriptor) {
	_panelDescriptor = panelDescriptor;
	_write = null;
	_tag = null;
    }
    
    public CreatePanelAction(PanelDescriptor panelDescriptor, Panel[] write) {
	_panelDescriptor = panelDescriptor;
	_write = write;
	_tag = null;
    }
    
    public CreatePanelAction(PanelDescriptor panelDescriptor, String tag) {
	_panelDescriptor = panelDescriptor;
	_write = null;
	_tag = tag;
    }
    
    public CreatePanelAction(PanelDescriptor panelDescriptor, Panel[] write, String tag) {
	_panelDescriptor = panelDescriptor;
	_write = write;
	_tag = tag;
    }

    @Override
    public void act(Component actOn) {
	Panel panel;
	if(_tag != null)
	    panel = UI.Instance.showPanelWithTag(_panelDescriptor, _tag);
	else
	    panel = UI.Instance.showPanel(_panelDescriptor);
	if(_write != null && _write.length > 0)
	    _write[0] = panel;
	    
    }
    
}
