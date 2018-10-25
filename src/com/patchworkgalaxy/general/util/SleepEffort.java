package com.patchworkgalaxy.general.util;

import java.util.concurrent.Callable;

public class SleepEffort implements Callable<Void> {
    
    private final int _duration;
    
    public SleepEffort(int duration) {
	_duration = duration;
    }

    @Override public Void call() throws Exception {
	Thread.sleep(_duration);
	return null;
    }    
    
}
