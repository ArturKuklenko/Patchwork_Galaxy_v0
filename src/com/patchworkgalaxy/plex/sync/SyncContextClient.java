package com.patchworkgalaxy.plex.sync;

import com.patchworkgalaxy.plex.Context;
import com.patchworkgalaxy.plex.Context.AsyncContext;
import com.patchworkgalaxy.plex.MonitorCallback;
import com.patchworkgalaxy.plex.Program;
import com.patchworkgalaxy.plex.RecordView;
import com.patchworkgalaxy.plex.Transaction;
import com.patchworkgalaxy.plex.VariableSpecifier;
import com.patchworkgalaxy.plex.exceptions.PlexDeserializationException;
import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import java.util.List;

public abstract class SyncContextClient implements AsyncContext {
    
    private Context _wraps;
    
    public SyncContextClient(Context wraps) {
	_wraps = wraps;
    }
    
    public SyncContextClient(Program program) {
	_wraps = program.getContext();
    }
    
    public abstract void sendTransaction(Transaction transaction);
    
    public abstract void onSyncFailed();
    
    public abstract void onTransactionFailed();
    
    public void acceptFromServer(Transaction transaction) {
	try {
	    _wraps.accept(transaction);
	} catch(PlexException e) { onTransactionFailed(); }
    }
    
    public void handleSyncResponse(List<String> syncResponse) {
	try {
	    _wraps = _wraps.load(syncResponse);
	} catch(PlexDeserializationException e) { onSyncFailed(); } 
    }

    @Override
    public RecordView getRecordView(String signature) throws PlexRecordException {
	return _wraps.getRecordView(signature);
    }

    @Override
    public void accept(Transaction transaction) {
	sendTransaction(transaction);
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
