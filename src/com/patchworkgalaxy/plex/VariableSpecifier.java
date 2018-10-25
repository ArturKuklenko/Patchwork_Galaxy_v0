package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexTypeException;
import java.util.Objects;

public class VariableSpecifier {
    
    private final String _categoryName, _variableName;
    
    public VariableSpecifier(String categoryName, String variableName) {
	if(categoryName == null) throw new IllegalArgumentException("Category name can't be null");
	if(variableName == null) throw new IllegalArgumentException("Variable name can't be null");
	_categoryName = categoryName;
	_variableName = variableName;
    }
    
    Internal getInteral(AbstractContext context) throws PlexTypeException {
	int catId = context.getCategoryId(_categoryName);
	Category category = context.getCategory(catId);
	int varId = category.getVarId(_variableName);
	return new Internal(context, catId, varId);
    }
    
    @Override
    public boolean equals(Object other) {
	if(other == null) return false;
	if(getClass() != other.getClass()) return false;
	return _categoryName.equals(((VariableSpecifier)other)._categoryName) &&
		_variableName.equals(((VariableSpecifier)other)._variableName);
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 83 * hash + Objects.hashCode(_categoryName);
	hash = 83 * hash + Objects.hashCode(_variableName);
	return hash;
    }
    
    class Internal extends VariableSpecifier {
	
	private final AbstractContext _context;
	private final int _categoryId, _variableId;
	
	private Internal(AbstractContext context, int categoryId, int variableId) {
	    super(VariableSpecifier.this._categoryName, VariableSpecifier.this._variableName);
	    _context = context;
	    _categoryId = categoryId;
	    _variableId = variableId;
	}
	
	int getCategoryId() {
	    return _categoryId;
	}
	
	int getVariableId() {
	    return _variableId;
	}
	
	Variable getVariable() {
	    Category category = _context.getCategory(_categoryId);
	    Variable variable = category.getDeclaredVariable(_variableId);
	    return variable;
	}
	
	@Override
	public boolean equals(Object other) {
	    //nb. super call checks types/null, ignore the warning
	    if(!super.equals(other)) return false;
	    return _context.equals(((Internal)other)._context);
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 41 * hash + Objects.hashCode(_context);
	    hash = 41 * hash + _categoryId;
	    hash = 41 * hash + _variableId;
	    return hash;
	}
	
    }
    
}
