package com.patchworkgalaxy.display.ui.defs.game;

import com.jme3.math.ColorRGBA;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.util.StandardTooltipDescriptor;
import com.patchworkgalaxy.game.commandcard.CommandCardEntry;

class CCButtonTooltipDescriptor extends StandardTooltipDescriptor {
    
    private final CommandCardEntry _entry;
    
    CCButtonTooltipDescriptor(CommandCardEntry entry) {
	super("");
	_entry = entry;
    }
    
    @Override
    public ColoredText getTooltipText() {
	ColoredText text = getTooltipHeader();
	return text.addText("\n\n").addText(_entry.getDescription());
    }
    
    private ColoredText getTooltipHeader() {
	ColoredText text = new ColoredText();
	String hotkey = _entry.getHotkey();
	if(hotkey != null && !hotkey.isEmpty())
	    text.addText("[" + hotkey + "] ", ColorRGBA.Yellow);
	text.addText(_entry.getDisplayName());
	if(_entry.isFake()) return text;
	int available = _entry.getAvailable();
	int max = _entry.getMax();
	if(available == 0 && max > 0) return text.addText(" [Cooldown cycle]", ColorRGBA.Blue);
	boolean affordable = _entry.isAffordable();
	int cost = _entry.getCost();
	if(cost > 0) {
	    if(affordable)
		text.addText(" [" + cost + " TB]", ColorRGBA.Green);
	    else
		return text.addText(" [" + cost + " TB]", ColorRGBA.Red);
	}
	if(max > 0)
	    text.addText(" [" + available + "/" + max + "]", ColorRGBA.Cyan);
	return text;
    }
    
}
