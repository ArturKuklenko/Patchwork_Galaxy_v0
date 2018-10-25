package com.patchworkgalaxy.display.ui.controller;

import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.UX2DControl;

class OldControlMutator {
    
    private final float _duration;
    private final UX2DControl _oldControl;
    
    OldControlMutator(UX2DControl oldControl) {
	_oldControl = oldControl;
	_duration = 0f;
    }
    
    OldControlMutator(UX2DControl oldControl, float duration) {
	_oldControl = oldControl;
	_duration = duration < 0 ? 0 : duration;
    }
    
    void mutate(ControlState oldControlState) {
	_oldControl.changeStateWithDuration(oldControlState, _duration);
    }
    
}
