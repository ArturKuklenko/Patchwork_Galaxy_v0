package com.patchworkgalaxy;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Effort {
    
    private final Future<?> _future;
    private final Set<Effect<Object>> _callbacks;
    private final Set<Effect<Throwable>> _errorHandlers;
    
    Effort(Future<?> future) {
	_future = future;
	_callbacks = new HashSet<>();
	_errorHandlers = new HashSet<>();
    }
        
    synchronized boolean check() {
	if(_future.isDone()) {
	    check0();
	    return true;
	}
	return false;
    }
    
    @SuppressWarnings("unchecked")
    public synchronized Effort then(Effect<?> callback) {
	_callbacks.add((Effect<Object>)callback);
	if(_future.isDone())
	    reschedule();
	return this;
    }
    
    public Effort then(Callable<?> callback) {
	return then(new EffectFromCallable<>(callback));
    }
    
    public Effort then(Runnable callback) {
	return then(new EffectFromCallable<>(callback));
    }
    
    public synchronized Effort onError(Effect<Throwable> handler) {
	_errorHandlers.add(handler);
	if(_future.isDone())
	    reschedule();
	return this;
    }
    
    public Effort onError(Callable<?> callback) {
	return then(new EffectFromCallable<Throwable>(callback));
    }
    
    public Effort onError(Runnable callback) {
	return then(new EffectFromCallable<Throwable>(callback));
    }
    
    private void check0() {
	try {
	    Object t = _future.get();
	    for(Effect<Object> callback : _callbacks)
		callback.execute(t);
	} catch(ExecutionException | InterruptedException | ClassCastException e) {
	    Exception e2 = e;
	    while(e2.getCause() instanceof Exception)
		e2 = (Exception)e2.getCause();
	    for(Effect<Throwable> callback : _errorHandlers)
		callback.execute(e);
	}
	_callbacks.clear();
	_errorHandlers.clear();
    }
    
    private void reschedule() {
	PatchworkGalaxy.schedule(new Callable<Void>() {
	    @Override public Void call() throws Exception {
		Effort.this.check();
		return null;
	    }
	});
    }
    
    private static class EffectFromCallable<T> implements Effect<T> {
	private final Callable<?> _callable;
	EffectFromCallable(final Runnable runnable) {
	    _callable = new Callable() {
		@Override public T call() throws Exception { runnable.run(); return null; }
	    };
	}
	EffectFromCallable(Callable<?> callable) {
	    _callable = callable;
	}
	@Override public void execute(T input) {
	    try { _callable.call(); }
	    catch(RuntimeException e) { throw e; }
	    catch(Exception e) { throw new RuntimeException(e); }
	} 
    }
    
}
