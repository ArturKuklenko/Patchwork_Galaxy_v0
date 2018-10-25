package com.patchworkgalaxy.general.data;

import com.patchworkgalaxy.general.util.TypedList;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameProps implements Cloneable {
    
    final Map<String, Object> contents;
    
    public GameProps() {
	contents = new HashMap<>();
    }
    
    public GameProps(GameProps copyOf) {
	contents = new HashMap<>(copyOf.contents);
    }
    
    public GameProps set(String key, Object value) {
	return mutable().set(key, value);
    }
    
    public GameProps mutable() {
	return new MutableGameProps(this);
    }
    
    public GameProps immutable() {
	return this;
    }
    
    /**
     * Gets the object stored at a given key. The {@code Class} paramater may be
     * used to constrain the object's type. If an object of the wrong type is
     * found, a {@link ClassCastException} is thrown (see below).
     * <p>
     * This method transparently handles {@link Resolver resolvers}. If a
     * resolver is encountered, it is resolved, and the resultant object handled
     * exactly as if it had been the one found (including the possible
     * {@code ClassCastException} if the object's type doesn't match).
     * </p><p>
     * There is a known issue with this method not properly handling resolvers
     * when a numeric type is requested.
     * </p>
     * @param <T> the requested type
     * @param t a Class object to signify the type parameter
     * @param key the key to look up
     * @return the found object
     */
    public <T> T get(Class<T> t, String key) {
	return convert(t, contents.get(key));
    }
    
    <T> List<T> convList(List<T> list) {
	return new ArrayList<>(list);
    }
    
    public <T> List<T> implyList(Class<T> type, String key) {
	
	List<T> list = getList(type, key);
	
	if(list == null) {
	    list = new TypedList<>(type);
	    set(key, list);
	}
	return convList(list);
	
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(Class<T> t, String key) {

	TypedList<?> l = get(TypedList.class, key);
	
	if(l == null)
	    return null;
	else
	    return convList(l.asListOfType(t));
	
    }
    
    public <T> T get(Class<T> t, String key, T defaultValue) {
	T result = get(t, key);
	if(result != null)
	    return result;
	else
	    return defaultValue;
    }
    
    private <T> T convert(Class<T> t, Object o) {
	o = resolver(o);
	if(o == null)
	    return null;
	if(Number.class.isAssignableFrom(t))
	    return convn(t, o);
	else
	    return t.cast(o);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T convn(Class<T> t, Object o) {
	if(!(o instanceof Number))
	    throw new ClassCastException();
	Number n = (Number)o;
	Number result;
	if(t == Float.class)
	    result = n.floatValue();
	else if(t == Integer.class)
	    result = n.intValue();
	else if(t == Double.class)
	    result = n.doubleValue();
	else if(t == Short.class)
	    result = n.shortValue();
	else if(t == Byte.class)
	    result = n.byteValue();
	else
	    throw new AssertionError();
	return t.cast(result);
    }
    
    private Object resolver(Object o) {
	return (o instanceof Resolver) ? ((Resolver)o).resolve() : o;
    }
    
    /**
     * Gets a stored float. nb. there is a known issue where this will not
     * properly handle a retrieved Numeric - use get(Numeric.class, key) and
     * manually invokve toFloat for now.
     * @param key lookup key
     * @return retrieved float
     */
    public float getFloat(String key) {
	Float f = get(Float.class, key);
	return f == null ? 0 : f;
    }
    
    /**
     * Gets a stored float. nb. there is a known issue where this will not
     * properly handle a retrieved Numeric - use get(Numeric.class, key) and
     * manually invokve toFloat (and cast) for now.
     * @param key lookup key
     * @return retrieved int
     */
    public int getInt(String key) {
	Integer i = get(Integer.class, key);
	return i == null ? 0 : i;
    }
    
    public String getString(String key) {
	return get(String.class, key);
    }
    
    public boolean getBoolean(String key) {
	Boolean tmp = get(Boolean.class, key);
	if(tmp == null)
	    return false;
	return tmp;
    }
    
    public Vector3f getVector3f(String key) {
	Vector3f temp = get(Vector3f.class, key);
	if(temp == null)
	    temp = Vector3f.ZERO;
	return new Vector3f(temp); //stupid mutable vector objects...
    }
    
    public boolean hasKey(String key) {
	return contents.containsKey(key);
    }
    
}
