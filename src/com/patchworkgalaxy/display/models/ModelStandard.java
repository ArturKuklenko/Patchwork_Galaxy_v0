package com.patchworkgalaxy.display.models;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.patchworkgalaxy.PatchworkGalaxy;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.general.util.Generator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModelStandard implements Model {
    
    private final GameProps _props;
    private Animation _priority;
    private final Spatial _spatial;
    private final Map<String, AnimationDef> _animations;
    private Positional _target;
    private final float _speed;
    private final float _scale;
    
    private final Generator<Vector3f> _random;
    private final Map<String, Generator<Vector3f>> _points;
    private final ModelSubscriptions _subscriptions;
    
    private Tinter _tinter;
    
    private boolean _moving;
    
    private static float MAGIC_SCALE_FACTOR = 2.3076923076923075f;
    
    ModelStandard(GameProps props, Spatial spatial, Map<String, AnimationDef> animations, Map<String, Generator<Vector3f>> points) {
	_props = props.immutable();
	_spatial = spatial;
	_animations = animations;
	_speed = _props.getFloat("Speed");
	_scale = computeScale();
	_random = AttachmentGeneratorFactory.getRandom(spatial, props.getFloat("Scale"));
	_points = points;
	_subscriptions = new ModelSubscriptions(this);
    }
    
    @Override public void setVisible(boolean visible) {
	if(visible) {
	    PatchworkGalaxy.rootNode().attachChild(_spatial);
	    _spatial.lookAt(getTarget(), Vector3f.UNIT_Z);
	}
	else
	    _spatial.removeFromParent();
    }
    
    @Override public void play(String animation) {
	AnimationDef def = _animations.get(animation);
	if(def == null)
	    def = FindAnimation.find(animation);
	if(def != null)
	    play(def);
    }

    @Override public void play(AnimationDef animation) {
	Animation a = animation.getAnimation(this);
	if(a.mode == Animation.Mode.PRIORITY)
	    prioritize(a);
	a.start();
    }

    private void prioritize(Animation animation) {
	if(_priority != null && !_priority.isDone())
	    _priority.end();
	_priority = animation;
    }
    
    @Override public void setTarget(Positional target) {
	_target = target;
    }

    @Override public Vector3f getTarget() {
	if(_target == null)
	    return getPositionVector().add(10, 0, 0);
	return _target.getPositionVector();
    }

    @Override public boolean blocking() {
	if(_priority != null) {
	    if(_priority.getSpatial() == null || _priority.isDone())
		_priority = null;
	}
	return _priority != null && !_priority.isDone();
    }

    @Override public Spatial getSpatial() {
	return _spatial;
    }

    @Override public float getSpeed() {
	return _speed;
    }
    
    @Override public float getScale() {
	return _scale;
    }
    
    private float computeScale() {
	BoundingBox bb = (BoundingBox)_spatial.getWorldBound();
	float scale = bb.getXExtent();
	scale = Math.max(scale, bb.getYExtent());
	scale = Math.max(scale, bb.getZExtent());
	scale *= MAGIC_SCALE_FACTOR;
	return scale;
    }

    @Override public void setPosition(Vector3f position) {
	_spatial.setLocalTranslation(position);
    }

    @Override public Vector3f getPositionVector() {
	return _spatial.getLocalTranslation();
    }
    
    @Override public void deprioritize(Animation animation) {
	if(animation == _priority)
	    _priority = null;
    }

    @Override public Vector3f getPoint(String type) {
	if(type == null)
	    return Vector3f.ZERO;
	else if(type.equals("Random"))
	    return _random.next();
	else if(_points.containsKey(type))
	    return _points.get(type).next();
	return Vector3f.ZERO;
    }

    @Override public boolean isVisible() {
	return _spatial.getParent() != null;
    }
    
    @Override public boolean isMoving() {
	return _moving;
    }
    
    @Override public void setMoving(boolean moving) {
	_moving = moving;
	Topic.GAME_CHRONO.update();
	_subscriptions.update(ModelSubscriptionType.MOTION);
    }

    @Override public void addSubscription(ModelSubscriptionType type, Subscriber<? super Model> subscriber) {
	_subscriptions.addSubscription(type, subscriber);
    }

    @Override public Subscribable getSubscribable(ModelSubscriptionType type) {
	return _subscriptions.getRawSubscribable(type);
    }
    
    @Override public void tint(final ColorRGBA color, final boolean immediate) {
	synchronized(this) {
	    if(_tinter == null) {
		_tinter = new Tinter();
		_spatial.addControl(_tinter);
	    }
	}
	_tinter.tint(color, immediate);
    }
    
}
