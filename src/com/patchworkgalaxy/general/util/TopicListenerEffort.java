package com.patchworkgalaxy.general.util;

import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import java.util.concurrent.Callable;

public class TopicListenerEffort implements Callable<Object>, Subscriber<Object> {
    
    private final Subscribable<?> _subscribable;
    private Object _result;
    private boolean _hasResult;
    
    public TopicListenerEffort(Subscribable<?> subscribable) {
	_subscribable = subscribable;
    }

    @Override public Object call() throws Exception {
	_subscribable.addSubscription(this);
	synchronized(this) {
	    while(!_hasResult)
		wait();
	}
	return _result;
    }

    @Override public void update(Subscribable<? extends Object> topic, Object message) {
	_result = message;
	_hasResult = true;
	synchronized(this) {
	    notifyAll();
	}
    }
    
}
