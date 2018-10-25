package com.patchworkgalaxy.plex;

public class VariableDeclaration {
    
    private final String _name, _type;
    
    public VariableDeclaration(String name, String type) {
	_name = name;
	_type = type;
    }
    
    String getName() {
	return _name;
    }
    
    String getTypeName() {
	return _type;
    }
    
}
