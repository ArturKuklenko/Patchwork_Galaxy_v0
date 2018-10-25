package com.patchworkgalaxy.general.subscriptions;

public class FastSubscribable<T> extends BaseSubscribable<T> implements Subscribable.Fast {
    
    @Override public void update(T message) {
	super.update(message);
	super.pushMessages();
    }
    
}
