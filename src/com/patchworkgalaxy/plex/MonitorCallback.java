package com.patchworkgalaxy.plex;

import java.util.Observable;

public interface MonitorCallback {
    
    void callback(RecordView recordView);
    
    public final class Observe extends Observable implements MonitorCallback {

	@Override
	public void callback(RecordView recordView) {
	    setChanged();
	    notifyObservers(recordView);
	}
	
    }
    
}
