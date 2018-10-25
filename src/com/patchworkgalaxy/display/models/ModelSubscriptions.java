package com.patchworkgalaxy.display.models;

import com.patchworkgalaxy.general.subscriptions.FastSubscribable;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import java.util.EnumMap;

class ModelSubscriptions {
    
    private final EnumMap<ModelSubscriptionType, Subscribable<Model>> _subscribables;
    private final Model _model;
    
    ModelSubscriptions(Model model) {
	_model = model;
	_subscribables = new EnumMap<>(ModelSubscriptionType.class);
    }
    
    Subscribable getRawSubscribable(ModelSubscriptionType type) {
	return _subscribables.get(type);
    }
    
    void addSubscription(ModelSubscriptionType type, Subscriber<? super Model> subscriber) {
	if(!_subscribables.containsKey(type))
	    _subscribables.put(type, new FastSubscribable<Model>());
	_subscribables.get(type).addSubscription(subscriber);
    }
    
    void update(ModelSubscriptionType type) {
	if(_subscribables.containsKey(type))
	    _subscribables.get(type).update(_model);
    }
    
}
