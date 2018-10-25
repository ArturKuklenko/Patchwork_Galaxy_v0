package com.patchworkgalaxy.display.models;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;

public interface Model extends Positional {
    
    void setVisible(boolean visible);
    
    boolean isVisible();
    
    void play(String animation);
    
    void play(AnimationDef animation);
    
    float getSpeed();
    
    float getScale();
    
    Spatial getSpatial();
    
    void setPosition(Vector3f position);
    
    void setTarget(Positional target);
    
    Vector3f getTarget();
    
    Vector3f getPoint(String type);
    
    boolean blocking();
    
    void deprioritize(Animation animation);
    
    boolean isMoving();
    
    void setMoving(boolean moving);
    
    Subscribable getSubscribable(ModelSubscriptionType type);
    
    void addSubscription(ModelSubscriptionType type, Subscriber<? super Model> subscriber);
    
    void tint(ColorRGBA color, boolean immediate);
    
}
