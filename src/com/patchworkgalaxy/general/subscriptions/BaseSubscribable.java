package com.patchworkgalaxy.general.subscriptions;

import com.patchworkgalaxy.PatchworkGalaxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseSubscribable<T> implements Subscribable<T> {
    
    private final Set<Subscriber<? super T>> _subscribers;
    private final List<T> _messages;
    
    public BaseSubscribable() {
	_subscribers = new HashSet<>(); //TODO: this should use weak references
	_messages = new ArrayList<>();
    }
    
    @Override public void addSubscription(Subscriber<? super T> subscriber) {
	_subscribers.add(subscriber);
    }
    
    @Override public void update() {
	update(null);
    }
    
    @Override public void update(T message) {
	boolean schedule;
	synchronized(_messages) {
	    schedule = _messages.isEmpty();
	    _messages.add(message);
	}
	if(schedule) {
	    PatchworkGalaxy.schedule(new Runnable() {
		@Override public void run() {
		    pushMessages();
		}
	    });
	}
    }
    
    protected void pushMessages() {
	List<T> mcopy;
	synchronized(_messages) {
	    mcopy = new ArrayList<>(_messages);
	    _messages.clear();
	}
	for(T message : mcopy) {
	    for(Subscriber<? super T> subscriber : new ArrayList<>(_subscribers)) {
		subscriber.update(this, message);
	    }
	}
		
    }
    
    protected Set<Subscriber<? super T>> getSubscribers() {
	return _subscribers;
    }
    
    protected List<T> getMessages() {
	return _messages;
    }
    
}
