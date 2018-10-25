package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.game.component.GameEvent;

public interface CanBeEvent {
    
    GameEvent toEvent(Object... params);
    
}
