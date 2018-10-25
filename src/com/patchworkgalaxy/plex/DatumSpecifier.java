package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexRecordException;
import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.Objects;

class DatumSpecifier {
    
    private final String _signature;
    private final int _variableId;
    
    DatumSpecifier(String signature, int variableId) {
	if(signature == null) throw new IllegalArgumentException("Signature can't be null");
	_signature = signature;
	_variableId = variableId;
    }
    
    Record getRecord(AbstractContext context) throws PlexRecordException {
	return context.getRecord(_signature);
    }
    
    int getVariableId() {
	return _variableId;
    }
    
    Datum getDatum(AbstractContext context) throws PlexTypeException, PlexRecordException {
	return getRecord(context).access(_variableId);
    }
    
    VariableSpecifier toVariableSpecifier(AbstractContext context) throws PlexRecordException, PlexTypeException {
	Category category = context.getRecord(_signature).getType();
	Variable variable = category.getDeclaredVariable(_variableId);
	return new VariableSpecifier(category.getName(), variable.getName());
    }
    
    boolean isVirtual() {
	return _variableId < 0;
    }
    
    DatumSpecifier toAnySpecifier() {
	return new DatumSpecifier(_signature, Definitions.ANY_UPDATE_ID);
    }
    
    @Override
    public boolean equals(Object o) {
	if(o == null) return false;
	if(getClass() != o.getClass()) return false;
	DatumSpecifier other = (DatumSpecifier)o;
	return _signature.equals(other._signature)
		&& _variableId == other._variableId;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 67 * hash + Objects.hashCode(_signature);
	hash = 67 * hash + _variableId;
	return hash;
    }
    
}
