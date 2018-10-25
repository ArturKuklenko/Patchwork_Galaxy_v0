package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexDeserializationException;
import com.patchworkgalaxy.plex.exceptions.PlexException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

abstract class AbstractContext implements Context {
    
    private final Program _program;
    private final MonitorGroup _userMonitors;
    
    AbstractContext(Program program) {
	_program = program;
	_userMonitors = new MonitorGroup();
    }
    
    AbstractContext(AbstractContext base) {
	_program = base._program;
	_userMonitors = new MonitorGroup(base._userMonitors);
    }
    
    abstract Record getRecord(String signature) throws PlexRecordException;
    
    abstract void addRecord(Record record) throws PlexRecordException;
    
    abstract Set<Record> getAllRecords();
    
    abstract Set<DatumSpecifier> acceptImpl(Transaction transaction) throws PlexException;
    
    String getValue(String signature, int key) throws PlexRecordException, PlexTypeException {
	Record record = getRecord(signature);
	Datum datum = record.access(key);
	if(datum == null)
	    datum = record.getType().getDefault(key);
	if(datum == null)
	    return record.getType().getDeclaredType(key).getNullValue();
	return datum.getValue();
    }
    
    Category getCategory(int id) {
	return _program.getCategory(id);
    }
    int getCategoryId(String name) throws PlexTypeException {
	return _program.getCategoryId(name);
    }
    Category getCategory(String name) throws PlexTypeException {
	return _program.getCategory(name);
    }
    
    Program getProgram() {
	return _program;
    }
    
    @Override
    public void attachMonitor(VariableSpecifier where, MonitorCallback callback) {
	_userMonitors.addMonitor(where, new MonitorStandard(callback));
    }
    
    @Override
    public synchronized void accept(Transaction transaction) throws PlexException {
	Set<DatumSpecifier> result = acceptImpl(transaction);
	_userMonitors.pokeMonitors(this, result);
    }
    
    @Override
    public RecordView getRecordView(String signature) throws PlexRecordException {
	return new RecordView(this, getRecord(signature));
    }
    
    @Override
    public List<String> dump() {
	List<String> result = new ArrayList<>();
	for(Record record : getAllRecords())
	    record.dump(result);
	return result;
    }
    
    @Override
    public AbstractContext load(List<String> dump) throws PlexDeserializationException {
	return new ContextDeserialized(this, dump);
    }
    
}
