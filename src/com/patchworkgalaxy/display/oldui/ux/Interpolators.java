package com.patchworkgalaxy.display.oldui.ux;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public final class Interpolators {
    
    private Interpolators() {}
    
    public static <T> T interpolate(T a, T b, float progress) {
	return interpolate(a, b, progress, null);
    }
    
    @SuppressWarnings("unchecked")
    static <T> T interpolate(T a, T b, float progress, InterpolationFunction ifunc) {
	Class<?> type = a.getClass();
	if(ifunc == null)
	    ifunc = LINEAR;
	Interpolator<T> interpolate;
	if(type.isAssignableFrom(Float.class) || type.isAssignableFrom(Float.TYPE))
	    interpolate = (Interpolator<T>)INTERPOLATE_FLOAT;
	else if(type.isAssignableFrom(Vector2f.class))
	    interpolate = (Interpolator<T>)INTERPOLATE_VECTOR2F;
	else if(type.isAssignableFrom(Vector3f.class))
	    interpolate = (Interpolator<T>)INTERPOLATE_VECTOR3F;
	else
	    interpolate = null;
	if(interpolate != null)
	    return interpolate.getInterpolation(a, b, progress, ifunc);
	else
	    return (Float.compare(progress, 1) == 0) ? b : a;
    }
    
    public static <T> float distanceBetween(T a, T b) {
	Class<?> type = (Class<?>)a.getClass();
	if(type.isAssignableFrom(Float.class) || type.isAssignableFrom(Float.TYPE))
	    return INTERPOLATE_FLOAT.distanceBetween((Float)a, (Float)b);
	else if(type.isAssignableFrom(Vector2f.class))
	    return INTERPOLATE_VECTOR2F.distanceBetween((Vector2f)a, (Vector2f)b);
	else if(type.isAssignableFrom(Vector3f.class))
	    return INTERPOLATE_VECTOR3F.distanceBetween((Vector3f)a, (Vector3f)b);
	else
	    return 0;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getValueForNullInput(Class<T> type) {
	if(type.isAssignableFrom(Float.class) || type.isAssignableFrom(Float.TYPE))
	    return (T)INTERPOLATE_FLOAT.getValuesForNullInput();
	else if(type.isAssignableFrom(Vector2f.class))
	    return (T)INTERPOLATE_VECTOR2F.getValuesForNullInput();
	else if(type.isAssignableFrom(Vector3f.class))
	    return (T)INTERPOLATE_VECTOR3F.getValuesForNullInput();
	else if(type.isAssignableFrom(Boolean.class))
	    return (T)Boolean.FALSE;
	else
	    return null;
    }
    
    private static final InterpolationFunction LINEAR = new InterpolationFunction() { @Override public float f(float x) { return x; } };
    
    private static final Interpolator<Float> INTERPOLATE_FLOAT = new Interpolator<Float> () {

	@Override
	public Float getInterpolation(Float a, Float b, float progress, InterpolationFunction ifunc) {
	    float p = ifunc.f(progress);
	    return a * (1 - p) + (b * p);
	}

	@Override
	public Float getValuesForNullInput() {
	    return 0f;
	}

	@Override
	public float distanceBetween(Float a, Float b) {
	    return Math.abs(a - b);
	}
    };
    
    private static final Interpolator<Vector2f> INTERPOLATE_VECTOR2F = new Interpolator<Vector2f>() {

	@Override
	public Vector2f getInterpolation(Vector2f a, Vector2f b, float progress, InterpolationFunction ifunc) {
	    float p = ifunc.f(progress);
	    Vector2f result = new Vector2f();
	    return result.interpolate(a, b, p);
	}

	@Override
	public Vector2f getValuesForNullInput() {
	    return new Vector2f(0f, 0f);
	}

	@Override
	public float distanceBetween(Vector2f a, Vector2f b) {
	    return a.distance(b);
	}
    
    };
    
    private static final Interpolator<Vector3f> INTERPOLATE_VECTOR3F = new Interpolator<Vector3f>() {

	@Override
	public Vector3f getInterpolation(Vector3f a, Vector3f b, float progress, InterpolationFunction ifunc) {
	    float p = ifunc.f(progress);
	    Vector3f result = new Vector3f();
	    return result.interpolate(a, b, p);
	}

	@Override
	public Vector3f getValuesForNullInput() {
	    return new Vector3f(0f, 0f, 0f);
	}

	@Override
	public float distanceBetween(Vector3f a, Vector3f b) {
	    return a.distance(b);
	}
    
    };
    
}
