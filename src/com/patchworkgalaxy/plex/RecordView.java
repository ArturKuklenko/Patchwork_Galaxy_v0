package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;

public class RecordView {
    
    private final AbstractContext _context;
    private final Record _record;
    
    RecordView(AbstractContext context, Record record) {
	_context = context;
	_record = record;
    }
    
    public String get(String name) throws PlexTypeException {
	return _record.accessValue(name);
    }
    
    public RecordView getLinkedRecordView(String name) throws PlexTypeException {
	Datum datum = _record.access(name);
	if(datum == null)
	    return null;
	if(!(datum.getType() instanceof Category))
	    throw new PlexTypeException(name + " does not indicate a datum of a record type");
	try {
	    Record record = _context.getRecord(datum.getValue());
	    return new RecordView(_context, record);
	} catch(PlexRecordException | NullPointerException e) { return null; }
    }
    
}
