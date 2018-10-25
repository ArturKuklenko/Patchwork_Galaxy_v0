package com.patchworkgalaxy.plex.sync;

import com.patchworkgalaxy.plex.Context;
import com.patchworkgalaxy.plex.MonitorCallback;
import com.patchworkgalaxy.plex.Program;
import com.patchworkgalaxy.plex.RecordView;
import com.patchworkgalaxy.plex.Transaction;
import com.patchworkgalaxy.plex.VariableSpecifier;
import com.patchworkgalaxy.plex.exceptions.PlexDeserializationException;
import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SyncContextServer implements Context {
    
    private final Context _wraps;
    private final Set<SyncServerConnection> _connections;
    
    public SyncContextServer(Context wraps) {
	_wraps = wraps;
	_connections = new HashSet<>();
    }
    
    public SyncContextServer(Program program) {
	_wraps = program.getContext();
	_connections = new HashSet<>();
    }
    
    public void addConnection(SyncServerConnection connection) {
	synchronized(_connections) {
	    _connections.add(connection);
	    connection.synchronize(dump());
	}
    }
    
    public void removeConnection(SyncServerConnection connection) {
	synchronized(_connections) {
	    _connections.remove(connection);
	}
    }
    
    @Override
    public RecordView getRecordView(String signature) throws PlexRecordException {
	return _wraps.getRecordView(signature);
    }

    @Override
    public void accept(Transaction transaction) throws PlexException {
	_wraps.accept(transaction);
	synchronized(_connections) {
	    for(SyncServerConnection connection : _connections)
		connection.accept(transaction);
	}
    }

    @Override
    public void attachMonitor(VariableSpecifier where, MonitorCallback callback) {
	_wraps.attachMonitor(where, callback);
    }

    @Override
    public List<String> dump() {
	return _wraps.dump();
    }

    @Override
    public Context load(List<String> dump) throws PlexDeserializationException {
	return _wraps.load(dump);
    }
    
}
