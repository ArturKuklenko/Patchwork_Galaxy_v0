package com.patchworkgalaxy.template;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.util.TypedList;
import com.patchworkgalaxy.general.util.Utils;

public abstract class ParserType<T> {
    
    private final Class<T> _type;
    
    ParserType(Class<T> type) {
	_type = type;
    }
    
    public Class<T> getType() {
	return _type;
    }
    
    public ParserType<TypedList> toListType() {
	return new ParserListType<>(_type, this);
    }
    
    public static final ParserType<String> STRING = new ParserType<String>(String.class) {
	@Override
	String check(GameProps props, String key, String value) {
	    return value;
	}
    };
    
    public static final ParserType<Integer> INTEGER = new ParserType<Integer>(Integer.class) {
	@Override
	Integer check(GameProps props, String key, String value) throws ParseException {
	    try {
		return Integer.valueOf(value);
	    }
	    catch(NumberFormatException e) {
		throw new ParseException("Expected integer");
	    }
	}
    };
    
    public static final ParserType<Float> FLOAT = new ParserType<Float>(Float.class) {
	@Override
	Float check(GameProps props, String key, String value) throws ParseException {
	    try {
		return Float.valueOf(value);
	    }
	    catch(NumberFormatException e) {
		throw new ParseException("Expected number");
	    }
	}
    };
    
    public static final ParserType<Boolean> MARKER = new ParserType<Boolean>(Boolean.class) {
	@Override
	Boolean check(GameProps props, String key, String value) {
	    return true;
	}
    };
    
    public static final ParserType<ColorRGBA> COLOR = new ParserType<ColorRGBA>(ColorRGBA.class) {
	@Override
	ColorRGBA check(GameProps props, String key, String value) throws ParseException {
	    try {
		try {
		    return Utils.parseColor(value);
		}
		catch(IllegalArgumentException e) {
		    throw new ParseException(e.getLocalizedMessage());
		}
	    }
	    catch(RuntimeException e) {
		throw new ParseException("Invalid color");
	    }
	}
    };
    
    public static final ParserType<Vector3f> VECTOR3F = new ParserType<Vector3f>(Vector3f.class) { 
	@Override
	Vector3f check(GameProps props, String key, String value) throws ParseException {
	    try {
		return Utils.parseVec(value);
	    }
	    catch(NumberFormatException | IndexOutOfBoundsException e) {
		throw new ParseException("Invalid vector3");
	    }
	}
    };
    
    public static final ParserType<Object> NO_OP = new ParserType<Object>(Object.class) {
	@Override
	Object check(GameProps props, String key, String value) {
	    return null;
	}
    };
    
    public static final ParserType<String> COMMA_STRING_ARRAY = new ParserType<String>(String.class) {
	@Override
	String check(GameProps props, String key, String value) {
	    String existing = props.getString(key);
	    if(existing == null)
		return value;
	    else {
		StringBuilder sb = new StringBuilder(existing);
		sb.append(",").append(value);
		return sb.toString();
	    }
	}
    };
    
    abstract T check(GameProps props, String key, String value) throws ParseException;
    
    public static final class ParserListType<T> extends ParserType<TypedList> {
	
	private final Class<T> _listType;
	private final ParserType<T> _parserType;
	
	ParserListType(Class<T> listType, ParserType<T> parserType) {
	    super(TypedList.class);
	    _listType = listType;
	    _parserType = parserType;
	}

	@Override
	@SuppressWarnings("unchecked")
	TypedList check(GameProps props, String key, String value) throws ParseException {
		
	    T append = _parserType.check(props, key, value);
	    if(append == null)
		return null;
	    
	    try {
		
		TypedList l1 = props.get(TypedList.class, key);
		TypedList<T> list;
		
		if(l1 == null)
		    list = new TypedList<>(_listType);
		else
		    list = new TypedList<>(l1.asListOfType(_listType));
		
		list.add(append);
		
		return list;
		
	    }
	    catch(ClassCastException e) {
		throw new ParseException(e);
	    }
	    
	}
	
	@Override
	public ParserType<TypedList> toListType() {
	    return this;
	}
	
	
    }
    
}
