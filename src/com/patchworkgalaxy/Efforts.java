package com.patchworkgalaxy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Efforts {
    private Efforts() {}
    
    private static final ExecutorService _execService = Executors.newFixedThreadPool(20);
    private static final List<Effort> _efforts = new LinkedList<>();
    
    public static Effort submit(Callable<?> callable) {
	Future<?> future = _execService.submit(callable);
	final Effort effort = new Effort(future);
	PatchworkGalaxy.schedule(new Callable<Void>() {
	    @Override public Void call() throws Exception {
		_efforts.add(effort);
		return null;
	    }
	});
	return effort;
    }
    
    static void check() {
	Iterator<Effort> i = _efforts.iterator();
	while(i.hasNext()) {
	    if(i.next().check())
		i.remove();
	}
    }
    
}
