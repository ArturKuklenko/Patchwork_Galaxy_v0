package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ContextStandard extends AbstractContext {
    
    private final Map<String, Record> _records;
    
    //private ContextScratch _scratchContext;
    
    ContextStandard(Program program) {
	super(program);
	_records = new HashMap<>();
    }
    
    ContextStandard(AbstractContext base) {
	super(base);
	_records = new HashMap<>();
    }

    @Override
    Record getRecord(String signature) throws PlexRecordException {
	Record record = _records.get(signature);
	if(record == null)
	    throw new PlexRecordException("Unknown record " + signature);
	return record;
    }
    
    @Override
    Set<Record> getAllRecords() {
	return new HashSet<>(_records.values());
    }
    
    @Override
    void addRecord(Record record) throws PlexRecordException {
	String signature = record.getSignature();
	if(_records.containsKey(signature))
	    throw new PlexRecordException("Duplicate record " + signature);
	_records.put(record.getSignature(), record);
    }

    @Override
    Set<DatumSpecifier> acceptImpl(Transaction transaction) throws PlexException {
	ContextScratch scratch = new ContextScratch(this);
	Set<DatumSpecifier> result = scratch.acceptImpl(transaction);
	merge(result, scratch);
	return result;
    }
    
    private void merge(Set<DatumSpecifier> changed, ContextScratch scratch) {
	try {
	    for(DatumSpecifier ds : changed) {
		Record record = ds.getRecord(scratch);
		String signature = record.getSignature();
		if(_records.containsKey(signature))
		    _records.get(signature).merge(record);
		else
		    _records.put(signature, record);
	    }
	} catch(PlexRecordException e) { throw new AssertionError(e); }
    }
    
}
