package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexDeserializationException;
import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import java.util.List;

public interface Context {
    
    RecordView getRecordView(String signature) throws PlexRecordException;

    void accept(Transaction transaction) throws PlexException;

    void attachMonitor(VariableSpecifier where, MonitorCallback callback);

    List<String> dump();
    
    Context load(List<String> dump) throws PlexDeserializationException;
    
    interface AsyncContext extends Context {}
    
    interface VolatileContext extends Context {}
    
    interface AsyncVolatileContext extends AsyncContext, VolatileContext {}
    
}
