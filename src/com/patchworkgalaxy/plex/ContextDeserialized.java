package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexDeserializationException;
import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class ContextDeserialized extends ContextStandard {
    
    ContextDeserialized(AbstractContext base, List<String> serializedRecords) throws PlexDeserializationException {
	super(base);
	Iterator<String> data = serializedRecords.iterator();
	try {
	    while(data.hasNext())
		populateRecord(data);
	} catch(NoSuchElementException e) {
	    throw new PlexDeserializationException("Bad serialized context: ended mid-record");
	} catch(PlexTypeException | PlexRecordException | RuntimeException e) {
	    throw new PlexDeserializationException(e);
	}
    }
    
    private void populateRecord(Iterator<String> data) throws PlexDeserializationException, PlexTypeException, PlexRecordException {
	String categoryName = data.next();
	String signature = data.next();
	Category category = getCategory(categoryName);
	Record record = getOrCreateRecord(signature, category);
	if(!record.getType().equals(category))
	    throw new PlexDeserializationException("Bad serialized context: type mismatch: " + record.getType());
	int vars = category.countVariables();
	for(int i = 0; i < vars; ++i) {
	    Type varType = category.getDeclaredType(i);
	    String value = data.next();
	    if(varType instanceof Category)
		getOrCreateRecord(value, (Category)varType);
	    record.setValue(this, i, value);
	}
    }
    
    private Record getOrCreateRecord(String signature, Category type) throws PlexRecordException {
	try {
	    return getRecord(signature);
	} catch(PlexRecordException e) {
	    Record record = new Record(type, signature);
	    addRecord(record);
	    return record;
	}
    }
    
}
