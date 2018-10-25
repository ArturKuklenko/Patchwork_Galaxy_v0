package com.patchworkgalaxy.display.oldui.ux;

public final class WriteRequest<T> {
    
    public static enum Priority {
	/**
	 * If the write can't be immediately performed, it will be ignored.
	 */
	LOW,
	
	/**
	 * If the write can't be immediately performed, and there is no
	 * waiting write, it will wait.
	 */
	NORMAL,
	
	/**
	 * If the write can't be immediately performed, it will wait,
	 * removing any already-waiting write in the process.
	 */
	HIGH,
	
	/**
	 * If the write can't be immediately performed, throw an AssertionError.
	 */
	N_A;
    }
    
    private final T _value;
    private final float _durationOrSpeed;
    private final boolean _durationFromDistance;
    
    private WriteRequest(T v, float f, boolean b) {
	_value = v;
	_durationOrSpeed = f;
	_durationFromDistance = b;
    }
    
    public static <T> WriteRequest<T> immediate(T value) {
	return new WriteRequest<>(value, 0, false);
    }
    
    public static <T> WriteRequest<T> withDuration(T value, float duration) {
	return new WriteRequest<>(value, duration, false);
    }
    
    public static <T> WriteRequest<T> withSpeed(T value, float speed) {
	return new WriteRequest<>(value, speed, true);
    }
    
    void apply(UXChannel<T> channel, ChannelMutex mutex) {
	apply(channel, mutex, Priority.NORMAL);
    }
   
    void apply(UXChannel<T> channel, ChannelMutex mutex, Priority priority) {
	channel.write(this, mutex, priority);
    }
    
    T getValue() {
	return _value;
    }
    
    float getDuration(T startingFrom) {
	float duration;
	if(_durationFromDistance) {
	    float distance = Interpolators.distanceBetween(startingFrom, _value);
	    duration = distance / _durationOrSpeed;
	}
	else
	    duration = _durationOrSpeed;
	return duration;
    }
    
}
