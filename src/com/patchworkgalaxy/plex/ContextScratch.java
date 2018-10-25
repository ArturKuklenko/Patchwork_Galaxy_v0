package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.Context.VolatileContext;
import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ContextScratch extends AbstractContext implements VolatileContext {
    
    private final ContextStandard _parent;
    private final Map<String, Record> _records;
    
    private final Set<DatumSpecifier> _scratch;
    
    ContextScratch(ContextStandard parent) {
	super(parent);
	_parent = parent;
	_records = new HashMap<>();
	_scratch = new HashSet<>();
    }

    @Override
    Record getRecord(String signature) throws PlexRecordException {
	if(!_records.containsKey(signature)) {
	    Record fromParent = new Record(_parent.getRecord(signature));
	    _records.put(signature, fromParent);
	    return fromParent;
	}
	return _records.get(signature); //this will never be null
    }
    
    @Override
    Set<Record> getAllRecords() {
	Set<Record> result = new HashSet<>(_parent.getAllRecords());
	result.addAll(_records.values());
	return result;
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
	for(Transaction t : transaction)
	    process(t);
	Set<DatumSpecifier> lastGeneration = new HashSet<>(_scratch);
	Set<DatumSpecifier> modified = new HashSet<>();
	_scratch.clear();
	while(!lastGeneration.isEmpty()) {
	    modified.addAll(lastGeneration);
	    getProgram().pokeRules(this, lastGeneration);
	    lastGeneration.clear();
	    lastGeneration.addAll(_scratch);
	    _scratch.clear();
	}
	return modified;
    }
    
    private void process(Transaction transaction) throws PlexTypeException, PlexRecordException {
	String signature = transaction.getSignature();
	switch(transaction.getMode()) {
	case CREATE:
	    String type = transaction.getType();
	    makeRecord(type, signature);
	    break;
	case WRITE:
	    String variableId = transaction.getVariable();
	    String value = transaction.getValue();
	    int key = getRecord(signature).getType().getVarId(variableId);
	    write(signature, key, value);
	    break;
	}
    }
    private Set<DatumSpecifier> write(String signature, int key, String value) throws PlexTypeException, PlexRecordException {
	Record record = getRecord(signature);
	_scratch.add(new DatumSpecifier(signature, key));
	record.setValue(this, key, value);
	return _scratch;
    }
    
    private Set<DatumSpecifier> makeRecord(String categoryId, String signature) throws PlexTypeException, PlexRecordException {
	Category category = getCategory(categoryId);
	Record record = new Record(category, signature);
	addRecord(record);
	if(signature == null) signature = record.getSignature();
	_scratch.add(new DatumSpecifier(signature, Definitions.INIT_ID));
	_scratch.add(new DatumSpecifier(signature, Definitions.DISPLAY_ID));
	return _scratch;
    }
    
    @Override
    Category getCategory(int categoryId) {
	return _parent.getCategory(categoryId);
    }
    
}
