package com.patchworkgalaxy.template.types;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.display.models.ParticleEmitterReverseFireball;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.Template;

public class SpewerTemplate extends Template<ParticleEmitter> {
    
    public SpewerTemplate(GameProps props) {
	super(props, "spewer");
    }
    
    @Override
    public ParticleEmitter instantiate(Object... params) {
	
	//find our scale...
	float scale = (params.length > 0 ? (Float)params[0] : 1);
	if(scale <= 0)
	    scale = 1;
	
	//load data from our properties
	String[] tokens = props.getString("Texture").split(",");
	String texture = "Effects/" + tokens[0];
	int x = Integer.valueOf(tokens[1]);
	int y = Integer.valueOf(tokens[2]);
	float speed = props.getFloat("Speed");
	float rate = props.getFloat("Rate");
	float lowDuration = props.getFloat("LowDuration");
	float highDuration = props.getFloat("HighDuration");
	ColorRGBA startColor = props.get(ColorRGBA.class, "StartColor");
	ColorRGBA endColor = props.get(ColorRGBA.class, "EndColor");
	float startSize = props.getFloat("StartSize");
	float endSize = props.getFloat("EndSize");
	Vector3f direction = props.getVector3f("Direction");
	float dirvar = props.getFloat("DirectionVariance");
	
	//construct an emitter to configure
	ParticleEmitter spewer = new ParticleEmitter(props.getString("Name"), ParticleMesh.Type.Triangle, (int)(rate * highDuration));
        Material mat = new Material(PatchworkGalaxy.assetManager(), "Common/MatDefs/Misc/Particle.j3md");
	mat.setTexture("Texture", PatchworkGalaxy.assetManager().loadTexture(texture));
	spewer.setMaterial(mat);
	
	//write data to the emitter
	spewer.setLowLife(lowDuration);
	spewer.setHighLife(highDuration);
	spewer.setParticlesPerSec(rate);
	spewer.setStartColor(startColor);
	spewer.setEndColor(endColor);
	spewer.setStartSize(startSize * scale);
	spewer.setEndSize(endSize * scale);
	
	//jmonkey uses images as spritesheets,
	//so we have to specify the dimensions of the spritesheet manually
	spewer.setImagesX(x);
	spewer.setImagesY(y);
	spewer.setSelectRandomImage(true);
	spewer.setFacingVelocity(props.getBoolean("FaceVel!"));
	
	//emitters need a velocity vector, let's give it one...
	//if the data file specifies one, we set its magnitude to our particle speed
	
	//if not, we check if it specifies a directional variance
	//if it does, we pick a random direction and set it to the appropriate length
	//otherwise, we're going to spawn particles in totally random directions,
	//so we just scale a unit vector to set the speed
	
	//if the emitter is scaled, we multiply by that as well
	if(direction == null || direction.equals(Vector3f.ZERO)) {
	    if(!props.hasKey("DirectionVariance")) {
		spewer.getParticleInfluencer().setInitialVelocity(new Vector3f(0, speed * scale, 0));
		spewer.getParticleInfluencer().setVelocityVariation(1);
	    }
	    else {
		Vector3f dir = new Vector3f((float)Math.random() - .5f, (float)Math.random() -.5f, (float)Math.random() - .5f).normalizeLocal();
		dir.multLocal(speed * scale);
		spewer.getParticleInfluencer().setInitialVelocity(dir);
		spewer.getParticleInfluencer().setVelocityVariation(dirvar);
	    }
	}
	else {
	    spewer.getParticleInfluencer().setInitialVelocity(direction.normalize().mult(speed * scale));
	    spewer.getParticleInfluencer().setVelocityVariation(dirvar);
	}
	
	return spewer;
    }
    public ParticleEmitterReverseFireball instantiateRFireball(int _shipHashCode, Object... params) {
	//find our scale...
	float scale = (params.length > 0 ? (Float)params[0] : 1);
	if(scale <= 0)
	    scale = 1;
	
	//load data from our properties
	String[] tokens = props.getString("Texture").split(",");
	String texture = "Effects/" + tokens[0];
	int x = Integer.valueOf(tokens[1]);
	int y = Integer.valueOf(tokens[2]);
	float speed = props.getFloat("Speed");
	float rate = props.getFloat("Rate");
	float lowDuration = props.getFloat("LowDuration");
	float highDuration = props.getFloat("HighDuration");
	ColorRGBA startColor = props.get(ColorRGBA.class, "StartColor");
	ColorRGBA endColor = props.get(ColorRGBA.class, "EndColor");
	float startSize = props.getFloat("StartSize");
	float endSize = props.getFloat("EndSize");
	Vector3f direction = props.getVector3f("Direction");
	float dirvar = props.getFloat("DirectionVariance");
	
	//construct an emitter to configure
	ParticleEmitterReverseFireball spewer = new ParticleEmitterReverseFireball(_shipHashCode,props.getString("Name"), ParticleMesh.Type.Triangle, (int)(rate * highDuration));
        Material mat = new Material(PatchworkGalaxy.assetManager(), "Common/MatDefs/Misc/Particle.j3md");
	mat.setTexture("Texture", PatchworkGalaxy.assetManager().loadTexture(texture));
	spewer.setMaterial(mat);
	
	//write data to the emitter
	spewer.setLowLife(lowDuration);
	spewer.setHighLife(highDuration);
	spewer.setParticlesPerSec(rate);
	spewer.setStartColor(startColor);
	spewer.setEndColor(endColor);
	spewer.setStartSize(startSize * scale);
	spewer.setEndSize(endSize * scale);
	
	//jmonkey uses images as spritesheets,
	//so we have to specify the dimensions of the spritesheet manually
	spewer.setImagesX(x);
	spewer.setImagesY(y);
	spewer.setSelectRandomImage(true);
	spewer.setFacingVelocity(props.getBoolean("FaceVel!"));
	
	//emitters need a velocity vector, let's give it one...
	//if the data file specifies one, we set its magnitude to our particle speed
	
	//if not, we check if it specifies a directional variance
	//if it does, we pick a random direction and set it to the appropriate length
	//otherwise, we're going to spawn particles in totally random directions,
	//so we just scale a unit vector to set the speed
	
	//if the emitter is scaled, we multiply by that as well
	if(direction == null || direction.equals(Vector3f.ZERO)) {
	    if(!props.hasKey("DirectionVariance")) {
		spewer.getParticleInfluencer().setInitialVelocity(new Vector3f(0, speed * scale, 0));
		spewer.getParticleInfluencer().setVelocityVariation(1);
	    }
	    else {
		Vector3f dir = new Vector3f((float)Math.random() - .5f, (float)Math.random() -.5f, (float)Math.random() - .5f).normalizeLocal();
		dir.multLocal(speed * scale);
		spewer.getParticleInfluencer().setInitialVelocity(dir);
		spewer.getParticleInfluencer().setVelocityVariation(dirvar);
	    }
	}
	else {
	    spewer.getParticleInfluencer().setInitialVelocity(direction.normalize().mult(speed * scale));
	    spewer.getParticleInfluencer().setVelocityVariation(dirvar);
	}
	
	return spewer;
    }
}
