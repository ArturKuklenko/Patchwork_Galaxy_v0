package com.patchworkgalaxy.display.models;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.patchworkgalaxy.general.util.Generator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


class AttachmentGeneratorFactory {
    
    private AttachmentGeneratorFactory() {}
    
    static Generator<Vector3f> DUMMY = getConstant(Vector3f.ZERO);
    
    static Generator<Vector3f> getRandom(Spatial spatial, float scale) {
	if(spatial instanceof Geometry)
	    return getRandom((Geometry)spatial, scale);
	else
	    return DUMMY;
    }
    
    private static Generator<Vector3f> getRandom(Geometry geometry, float scale) {
	final Mesh mesh = geometry.getMesh();
	final int count = mesh.getTriangleCount();
	final Vector3f a = new Vector3f(),
		    b = new Vector3f(),
		    c = new Vector3f();
	final float fscale = scale;
	
	return new Generator<Vector3f>() {
	    @Override
	    public Vector3f next() {
		int index = (int)Math.floor(Math.random() * count);
		mesh.getTriangle(index, a, b, c);
		a.multLocal(fscale);
		return a;
	    }
	};
	
    }
    
    private static Generator<Vector3f> getConstant(final Vector3f constant) {
	return new Generator<Vector3f>() {
	    @Override
	    public Vector3f next() {
		return new Vector3f(constant);
	    }	    
	};
    }
    
    static Generator<Vector3f> getIteration(List<Vector3f> list) {
	
	if(list.isEmpty()) throw new IllegalArgumentException();
	
	final LinkedList<Vector3f> flist = new LinkedList<>(list);
	return new Generator<Vector3f>() {
	    Iterator<Vector3f> i = flist.iterator();
	    @Override
	    public Vector3f next() {
		if(i.hasNext())
		    return i.next();
		else {
		    i = flist.iterator();
		    return i.next();
		}
	    }
	};
    }
    
}
