package com.patchworkgalaxy.general.lang;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Localizer {
    
    private Localizer() {}
    
    private static Random _random = new Random();;
    private static Map<String, LocalizationNamespace> _namespaces = new HashMap<>();
    
    public static void setLocalization(String locationfile) {
	try {
	    _namespaces = Loader.create(locationfile).load();
	} catch(IOException e) {
	    throw new RuntimeException(e);
	}
    }
    
    public static String getLocalizedString(Localizable localizable, String key, Random random) {
	return getLocalizedString(localizable.getLocalizationNamespaces(), key, random);
    }
    
    public static String getLocalizedString(Localizable localizable, String key) {
	return getLocalizedString(localizable.getLocalizationNamespaces(), key, _random);
    }
    
    public static String getLocalizedString(String[] namespaces, String key) {
	return getLocalizedString(namespaces, key, _random);
    }
    
    public static String getLocalizedString(String namespace, String key) {
	return getLocalizedString(new String[] { namespace }, key);
    }
    
    public static String getLocalizedString(String[] namespaces, String key, Random random) {
	String result = null;
	for(String namespace : namespaces) {
	    if(!_namespaces.containsKey(namespace)) continue;
	    result = _namespaces.get(namespace).getValue(random, key);
	    if(result != null) break;
	}
	if(result == null) result = "";
	return result;
    }
    
}
