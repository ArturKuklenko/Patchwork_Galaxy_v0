package com.patchworkgalaxy.display.models;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.util.Generator;
import com.patchworkgalaxy.general.util.ListMap;
import com.patchworkgalaxy.general.util.Utils;
import com.patchworkgalaxy.template.types.ModelTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelFactory {
    
    private final AssetManager _assetManager;
    
    private static ModelFactory STD_TMP;
    public static ModelFactory getStandard() {
	if(STD_TMP == null)
	    STD_TMP = new ModelFactory(PatchworkGalaxy.assetManager());
	return STD_TMP;
    }
    
    public static final ModelFactory FAKE = new ModelFactory(null);
    
    public ModelFactory(AssetManager assetManager) {
	_assetManager = assetManager;
    }
    
    public Model getModel(ModelTemplate template) {
	return getModel(template.getProps());
    }
    
    public Model getModel(GameProps modelProps) {
	
	if(_assetManager == null)
	    return new ModelFake();
	
	Spatial spatial = getSpatial(modelProps);
	Map<String, AnimationDef> animations = getAnimationMap(modelProps);
	Map<String, Generator<Vector3f>> points = getPointsMap(modelProps);
	
	ModelStandard m = new ModelStandard(modelProps, spatial, animations, points);
	spatial.addControl(new ModelControl(m));
	return m;
	
    }
    
    private Spatial getSpatial(GameProps modelProps) {
	String modelName = modelProps.getString("File");
	if(modelName == null)
	    throw new IllegalArgumentException("Attempted to create model w/o filename");
	
	Spatial spatial = _assetManager.loadModel("Models/" + modelName);
	if(spatial == null)
	    throw new AssetNotFoundException("Couldn't find model " + modelName);
	
	float scale = modelProps.getFloat("Scale");
	if(scale > 0)
	    spatial.setLocalScale(scale);
	
	if(spatial instanceof Geometry) {
	    Geometry geom = (Geometry)spatial;
	    Material mat = geom.getMaterial();
	    if(mat == null)
		mat = new Material(_assetManager, "Common/MatDefs/Light/Lighting.j3md");
	    mat.setBoolean("UseMaterialColors",true);
	    geom.setMaterial(mat);
	}
	
	return spatial;
	
    }
    
    private Map<String, AnimationDef> getAnimationMap(GameProps modelProps) {
	
	@SuppressWarnings("unchecked")
	List<AnimationDef> alist = modelProps.get(List.class, "Animation");
	Map<String, AnimationDef> animations = new HashMap<>();
	
	if(alist != null) {
	    for(AnimationDef def : alist) {
		animations.put(def.getTrigger(), def);
	    }
	}
	
	return animations;
	
    }
    
    private Map<String, Generator<Vector3f>> getPointsMap(GameProps modelProps) {
	
	ListMap<String, Vector3f> temp = new ListMap<>();
	Map<String, Generator<Vector3f>> result = new HashMap<>();
	
	String p = modelProps.getString("Point");
	if(p == null)
	    return result;
	
	String[] points = p.split(",");
	for(String point : points) {
	    String[] split = point.trim().split(":");
	    String name = split[0].trim();
	    Vector3f vec = Utils.parseVec(split[1].trim().split(" "));
	    temp.append(name, vec);
	}
	for(String key : temp.keySet()) {
	    List<Vector3f> list = temp.get(key);
	    Generator<Vector3f> generator = AttachmentGeneratorFactory.getIteration(list);
	    result.put(key, generator);
	}
	
	return result;
	
    }
    
}
