package com.patchworkgalaxy.display.oldui.ux;

import com.patchworkgalaxy.display.oldui.ux.WriteRequest.Priority;
import java.util.Observable;

public class UXChannel<T> extends Observable {
    
    private ChannelMutex _mutex;
    private WriteRequest<T> _waitingWrite;
    
    private T _value, _targetValue, _oldValue;
    private float _interpolationDuration, _progress;
    
    UXChannel(T initial) {
	_value = _targetValue = _oldValue = initial;
    }
    
    public static <T> UXChannel<T> create(T initial) {
	return new UXChannel<>(initial);
    }
    
    public WriteRequest<T> getImmediateWriteRequest(T value, float speed) {
	return WriteRequest.immediate(value);
    }
    
    public WriteRequest<T> getWriteRequestWithSpeed(T value, float speed) {
	return WriteRequest.withSpeed(value, speed);
    }
    
    public WriteRequest<T> getWriteRequestWithDuration(T value, float duration) {
	return WriteRequest.withDuration(value, duration);
    }
    
    public void easyWrite(T value) {
	write(getWriteRequestWithDuration(value, 0));
    }
    
    public void easyWriteWithSpeed(T value, float speed) {
	write(getWriteRequestWithSpeed(value, speed));
    }
    
    public void easyWriteWithDuration(T value, float duration) {
	write(getWriteRequestWithDuration(value, duration));
    }
    
    void update(float tpf) {
	if(_progress < 1) {
	    if(_interpolationDuration > 0) {
		_progress += tpf / _interpolationDuration;
		if(_progress > 1)
		    _progress = 1;
		_value = doInterpolation();
	    }
	    else {
		_value = _targetValue;
		_progress = 1;
	    }
	    setChanged();
	    notifyObservers(UXChannel.class);
	}
    }
    
    T doInterpolation() {
	return Interpolators.interpolate(_oldValue, _targetValue, _progress);
    }
    
    public void write(WriteRequest<T> request) {
	write(request, null, null);
    }
    
    public void write(WriteRequest<T> request, ChannelMutex mutex) {
	write(request, mutex, null);
    }
    
    public void write(WriteRequest<T> request, Priority priority) {
	write(request, null, priority);
    }
    
    public void write(WriteRequest<T> request, ChannelMutex mutex, Priority priority) {
	if(_mutex != null && mutex != _mutex)
	    delayedWrite(request, priority);
	else {
	    _oldValue = _value;
	    _targetValue = request.getValue();
	    _interpolationDuration = request.getDuration(_oldValue);
	    _progress = 0;
	    update(0);
	}
    }
    
    @SuppressWarnings("fallthrough")
    private void delayedWrite(WriteRequest<T> request, Priority priority) {
	if(priority == null)
	    priority = Priority.NORMAL;
	switch(priority) {
	case N_A:
	    throw new AssertionError();
	case LOW:
	    break;
	case NORMAL:
	    if(_waitingWrite != null)
		break;
	case HIGH:  //the fall-through is intentional
	    _waitingWrite = request;
	}
    }
    
    public T getValue() {
	return _value;
    }
    
    public ChannelMutex acquireMutex() {
	if(_mutex == null) {
	    _mutex = new ChannelMutex(this);
	    return _mutex;
	}
	else
	    return null;
    }
    
    boolean setMutex(UXMutex mutex) {
	if(_mutex == null) {
	    _mutex = mutex;
	    mutex.add(this);
	    return true;
	}
	else
	    return false;
    }
    
    public boolean releaseMutex(ChannelMutex mutex) {
	if(mutex == _mutex) {
	    _mutex = null;
	    if(_waitingWrite != null) {
		_waitingWrite.apply(this, null, Priority.N_A);
		_waitingWrite = null;
	    }
	    return true;
	}
	else
	    return false;
    }
    
}
