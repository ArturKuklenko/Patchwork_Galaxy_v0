package com.patchworkgalaxy.template;

import com.patchworkgalaxy.general.data.GameProps;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Parser<T> {
    
    static String currentFile;
    static int currentLine;
    
    private final TemplateRegistry<T> _registry;
    private final BufferedReader _reader;
    private final String _filename;
    
    private String _line;
    
    protected Parser(TemplateRegistry<T> registry, BufferedReader reader, String filename) {
	_registry = registry;
	_reader = reader;
	_filename = filename;
    }
    
    protected Parser(BufferedReader reader) {
	_registry = null;
	_reader = reader;
	_filename = null;
    }
    
    protected final String getLine() {
	return _line;
    }
    
    protected abstract GameProps section() throws ParseException;
    
    protected abstract void interpret(TemplateRegistry<T> registry, GameProps props) throws ParseException;
    
    public void parse() throws ParseException {
	try {
	    parse0();
	}
	catch(RuntimeException e) {
	    throw new ParseException(e);
	}
    }
    
    private void parse0() throws ParseException {
	if(_filename != null) {
	    currentFile = _filename;
	    currentLine = 0;
	}
	if(_line == null)
	    advance();
	while(_line != null) {
	    GameProps section = section();
	    if(section != null)
		interpret(_registry, section);
	    else
		break;
	}
    }
    
    protected String allow(GameProps props, String key, ParserType type) throws ParseException {
	if(_line == null)
	    return null;
	String[] split = _line.split("=");
	if(split[0].equals(key)) {
	    String value = split.length > 1 ? split[1] : "";
	    
	    Object o = type.check(props, key, value);
	    if(o != null)
		props.set(key, o);
		
	    advance();
	    return value;
	}
	else
	    return null;
    }
    
    protected String require(GameProps props, String key, ParserType type) throws ParseException {
	if(_line == null)
	    throw new ParseException(new StringBuilder("File ended but expected ").append(key).toString());
	String[] split = _line.split("=");
	if(split[0].equals(key)) {
	    String value = split.length > 1 ? split[1] : null;
	    
	    Object o = type.check(props, key, value);
	    if(o != null)
		props.set(key, o);
	    advance();
	    return value;
	}
	else {
	    throw new ParseException(new StringBuilder("Found ").append(split[0]).append(" but expected ").append(key).toString());
	}
    }
    
    @SuppressWarnings("empty-statement")
    protected void repeat(GameProps props, String key) throws ParseException {
	while(allow(props, key, ParserType.COMMA_STRING_ARRAY) != null);
    }
    
    @SuppressWarnings("unchecked")
    protected <T extends Parser> T subparser(Class<T> type, Object... props) throws ParseException {
	try {
	    Constructor<T> c = type.getDeclaredConstructor(BufferedReader.class, Object[].class);
	    c.setAccessible(true);
	    Parser p = c.newInstance(_reader, props);
	    p._line = _line;
	    p.parse();
	    _line = p._line;
	    return (T)p;
	}
	catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
	    throw new ParseException(type + " is not suitable for use as a subparser");
	}
    }
    
    private boolean advance() throws ParseException {
	String result = getNextLine();
	while(result != null && result.length() == 0)
	    result = getNextLine();
	_line = result;
	return result != null;
    }
    
    private String getNextLine() throws ParseException {
	try {
	    String result = _reader.readLine();
	    if(result == null)
		return null;
	    result = result.trim();
	    ++currentLine;
	    return result;
	}
	catch(IOException e) {
	    throw new ParseException(e);
	}
    }
    

    
}
