package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexTypeException;

interface Type {
    
    void isAssignableAs(Type other) throws PlexTypeException;
    
    String getNullValue();
    
    Datum createDatum(String value, AbstractContext context) throws PlexTypeException;
    
    String getName();
    
}