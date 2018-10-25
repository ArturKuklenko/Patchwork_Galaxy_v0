package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;

/**
 * Something that has a position, represented as a {@link Vector3f vector}.
 * @author redacted
 * @see FromVector
 */
public interface Positional {
   
    /**
     * @return Gets the {@link Vector3f} representing this object's position.
     */
    Vector3f getPositionVector();
    
    /**
     * A wrapper around a {@link Vector3f}. Its position is that vector
     * (using the values the vector had as it was passed to the constructor).
     * <p>
     * If a {@link Positional} is needed but you only have a raw {@link Vector3f},
     * use {@code new Positional.FromVector(foo)}, where {@code foo} is the vector.
     * </p>
     */
    public static final class FromVector implements Positional {

	private final Vector3f _vector;
	
	public FromVector(Vector3f vector) {
	    _vector = new Vector3f(vector);
	}
	
	public FromVector(float x, float y, float z) {
	    _vector = new Vector3f(x, y, z);
	}
	
	@Override
	public Vector3f getPositionVector() {
	    return _vector;
	}
	
    }
    
}
