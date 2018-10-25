package com.patchworkgalaxy.general.lang;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class LoadMerger {
    
    private final Map<String, LocalizedString> _values;
    
    LoadMerger(Map<String, LocalizedString> values) {
	_values = values;
    }
    
    Map<String, LocalizationNamespace> merge() {
	Map<String, Map<String, LocalizedString>> scratch = new HashMap<>();
	for(Entry<String, LocalizedString> i : _values.entrySet()) {
	    String[] namespaceAndKey = getNamespaceAndKey(i.getKey());
	    String namespace = namespaceAndKey[0];
	    String key = namespaceAndKey[1];
	    LocalizedString localization = i.getValue();
	    if(!scratch.containsKey(namespace))
		scratch.put(namespace, new HashMap<String, LocalizedString>());
	    scratch.get(namespace).put(key, localization);
	}
	Map<String, LocalizationNamespace> result = new HashMap<>();
	for(Entry<String, Map<String, LocalizedString>> i : scratch.entrySet()) {
	    LocalizationNamespace namespace = new LocalizationNamespace(i.getValue());
	    result.put(i.getKey(), namespace);
	}
	return result;
    }
    
    static String[] getNamespaceAndKey(String longname) {
	//returns an array foo such that
	//foo[0] is the namespace
	//foo[1] is a key into that namespace
	//the implementation is kind of mucky, so I've left the method package-visible
	//to faciliate defining tests
	String[] segments = longname.split("\\.");
	int len = segments.length - 1;
	String key = segments[len];
	if(len == 0) return new String[] {"", key};
	StringBuilder namespace = new StringBuilder(segments[0]);
	for(int i = 1; i < len; ++i)
	    namespace.append(".").append(segments[i]);
	return new String[] {namespace.toString(), key};
    }
    
}
