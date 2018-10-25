package com.patchworkgalaxy;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import java.util.HashSet;
import java.util.Set;

public class Collisions {
    private Collisions() {}
    
    private static final float STALE_THRESHOLD = .1f;
    private static float _staleness = 0f;
    
    private static final Set<Spatial> _lastResults = new HashSet<>();
    
    public static boolean isMouseOver(Vector2f mouse, Spatial spatial) {
	if(_staleness > STALE_THRESHOLD)
	    refresh(mouse);
	return _lastResults.contains(spatial);
    }
    
    static void update(float tpf) {
	_staleness += tpf;
    }
    
    private static void refresh(Vector2f mouse) {
	_staleness = 0;
	_lastResults.clear();
	Camera cam = PatchworkGalaxy.camera();
	CollisionResults results = new CollisionResults();
	Vector3f click3d = cam.getWorldCoordinates(new Vector2f(mouse.x, mouse.y), 0.0F).clone();
	Vector3f dir = cam.getWorldCoordinates(new Vector2f(mouse.x, mouse.y), 1.0F).subtractLocal(click3d).normalizeLocal();
	Ray ray = new Ray(click3d, dir);
	PatchworkGalaxy.rootNode().collideWith(ray, results);
	for(CollisionResult result : results)
	    _lastResults.add(result.getGeometry());
    }
    
}