package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexRuleException;

class MonitorStandard implements Monitor {
    
    private final MonitorCallback _callback;
    
    MonitorStandard(MonitorCallback callback) {
	_callback = callback;
    }
    
    @Override
    public void onPoked(AbstractContext context, Record trigger) throws PlexRuleException {
	try {
	    _callback.callback(new RecordView(context, trigger));
	} catch(RuntimeException e) { throw new PlexRuleException(e); }
    }
    
}
