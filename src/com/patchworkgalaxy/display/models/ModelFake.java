package com.patchworkgalaxy.display.models;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;

class ModelFake implements Model {
    
    ModelFake() {}
    
    @Override public void setVisible(boolean visible) {}
    
    @Override public void play(String animation) {}

    @Override public void play(AnimationDef animation) {}

    @Override public float getSpeed() {
	return 50;
    }
    
    @Override public float getScale() {
	return 1;
    }

    @Override public Spatial getSpatial() {
	return null;
    }

    @Override public void setPosition(Vector3f position) {}

    @Override public Vector3f getPositionVector() {
	return null;
    }

    @Override public Vector3f getPoint(String type) {
	return null;
    }

    @Override public void setTarget(Positional target) {}

    @Override public Vector3f getTarget() {
	return null;
    }

    @Override public boolean blocking() {
	return false;
    }
    
    @Override public void deprioritize(Animation animation) {}
    
    @Override public boolean isVisible() {
	return false;
    }
    
    @Override public boolean isMoving() {
	return false;
    }
    
    @Override public void setMoving(boolean moving) {}

    @Override public void addSubscription(ModelSubscriptionType type, Subscriber<? super Model> subscriber) {}
    
    @Override public Subscribable getSubscribable(ModelSubscriptionType type) {
	return null;
    }
    
    @Override public void tint(ColorRGBA color, boolean immediate) {}
    
}
