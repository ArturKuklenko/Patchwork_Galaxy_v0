package com.patchworkgalaxy.general.subscriptions;

public enum Topic implements Subscribable<Object> {
    
    GAME_STARTED,
    GAME_ENDED,
    GAME_CHRONO,
    
    CLIENT_CONNECTED,
    CLIENT_DISCONNECTED,
    CLIENT_NET_RESPONSE,
    
    UI_KEY_PRESSED,
    UI_CONTROL_CLICKED
    ;
    
    private final BaseSubscribable<Object> _wraps;
    
    private Topic() {
	_wraps = new BaseSubscribable<>();
    }    
    
    @Override public void addSubscription(Subscriber<Object> subscriber) {
	_wraps.addSubscription(subscriber);
    }
    
    @Override public void update() {
	_wraps.update(null);
    }
    
    @Override public void update(Object message) {
	_wraps.update(message);
    }
    
}
