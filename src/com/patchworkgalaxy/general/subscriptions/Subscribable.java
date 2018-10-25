package com.patchworkgalaxy.general.subscriptions;

public interface Subscribable<T> {
    
    static interface Fast {}
    
    void addSubscription(Subscriber<? super T> subscriber);
    
    public void update();
    
    public void update(T message);
    
}
