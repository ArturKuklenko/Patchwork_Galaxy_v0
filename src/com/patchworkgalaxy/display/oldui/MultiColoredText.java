package com.patchworkgalaxy.display.oldui;

import java.util.ArrayList;
import java.util.List;

class MultiColoredText {
    
    private final List<ColoredText> _texts;
    private final List<Float> _durations;
    private final int _size;
    private final boolean _timeout;
    private static final float MESSAGE_DISPLAY_TIME = 10;
    
    
    MultiColoredText(int size, boolean timeout) {
	_texts = new ArrayList<>();
	_durations = new ArrayList<>();
	_size = size;
	_timeout = timeout;
    }
    
    void enqueueText(ColoredText newtext) {
	_texts.add(newtext);
	_durations.add(0f);
	if(_texts.size() > _size) {
	    _texts.remove(0);
	    _durations.remove(0);
	}
    }
    
    ColoredText getText() {
	ColoredText text = new ColoredText();
	int len = _texts.size();
	for(int i = len; i < _size; ++i) {
	    text.addText("\n");
	}
	for(int i = 0; i < len; ++i)
		text.spliceOtherText(_texts.get(i), "\n");
	return text;
    }
    
    boolean update(float tpf) {
	if(!_timeout)
	    return false;
	boolean updated = false;
	for(int i = _texts.size(); --i >= 0;) {
	    float duration = _durations.get(i);
	    duration += tpf;
	    if(duration > MESSAGE_DISPLAY_TIME) {
		_durations.remove(i);
		_texts.remove(i);
		updated = true;
	    }
	    else
		_durations.set(i, duration);
	}
	return updated;
    }
    
}
