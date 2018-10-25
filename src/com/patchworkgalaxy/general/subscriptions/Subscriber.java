package com.patchworkgalaxy.general.subscriptions;

public interface Subscriber<T> {
    
    void update(Subscribable<? extends T> topic, T message);
    
}
