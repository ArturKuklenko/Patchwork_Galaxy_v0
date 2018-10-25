package com.patchworkgalaxy.general.lang;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import lang.Langfiles;

class Loader {
    
    private final BufferedReader _br;
    
    Loader(BufferedReader br) {
	_br = br;
    }
    
    static Loader create(String filename) throws IOException {
	BufferedReader br;
	try {
	     br = new BufferedReader(new FileReader("data/lang/" + filename));
	}
	catch(FileNotFoundException e) {
	    br = Langfiles.getReader(filename);
	}
	return new Loader(br);
    }
    
    Map<String, LocalizationNamespace> load() throws IOException {
	try(BufferedReader br = _br) {
	    LoadHelper helper = new LoadHelper();
	    String line;
	    while((line = br.readLine()) != null) {
		if(line.isEmpty()) continue;
		String[] split = line.split("=");
		if(split.length != 2)
		    throw new IllegalArgumentException("Bad line " + line);
		helper.addMapping(split[0], split[1]);
	    }
	    return new LoadMerger(helper.getMappings()).merge();
	}
    }
    
}
