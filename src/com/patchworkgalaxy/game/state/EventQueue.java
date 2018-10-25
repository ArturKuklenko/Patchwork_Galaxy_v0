package com.patchworkgalaxy.game.state;

import com.patchworkgalaxy.game.component.GameEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class EventQueue {
    
    private final LinkedList<GameEvent> _queue;
    
    private boolean _active;
    private final boolean _fast;
    
    private static final Comparator<GameEvent> _comparePriorities
	    = new Comparator<GameEvent>() {
		@Override
		public int compare(GameEvent o1, GameEvent o2) {
		    return o2.getPriority() - o1.getPriority();
		}
	    };
    
    public EventQueue(boolean fast) {
	_queue = new LinkedList<>();
	_fast = fast;
    }
    
    public void enqueue(GameEvent event) {
	
	if(_fast)
	    event.process();
	else
	    _queue.add(event);
	
	if(_queue.size() > 1)
	    prioritySort();
	
    }
    
    public void processEvents() {
	
	if(_active)
	    return;
	
	_active = true;
	
	while(!_queue.isEmpty())
	    _queue.removeFirst().process();
	
	_active = false;
	
	
    }
    
    private void prioritySort() {
	
	assert(_queue.size() > 2);
	
	int p1 = _queue.getLast().getPriority();
	int p2 = _queue.get(_queue.size() - 2).getPriority();
	
	if(p1 != p2)
	    Collections.sort(_queue, _comparePriorities);
	
    }
    
}
