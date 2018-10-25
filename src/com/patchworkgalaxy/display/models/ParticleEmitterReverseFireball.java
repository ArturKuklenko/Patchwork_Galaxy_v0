/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patchworkgalaxy.display.models;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import java.util.ArrayList;

/**
 *
 * wrapper class for ParticleEmitter
 */
public class ParticleEmitterReverseFireball extends ParticleEmitter {
    	
    private int shipHashCode;
    public ParticleEmitterReverseFireball(int shipHashCode, String name, ParticleMesh.Type type, int numParticles) {
        super(name, type, numParticles);
        this.shipHashCode = shipHashCode;
    }
    public int getShipHashCode() {
        return shipHashCode;
    }
    
}
