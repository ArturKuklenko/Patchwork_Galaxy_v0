package com.patchworkgalaxy.display.ui.util.prefab;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Collisions;
import com.patchworkgalaxy.display.models.Model;
import com.patchworkgalaxy.display.models.ModelSubscriptionType;
import com.patchworkgalaxy.display.oldui.ControlFactory;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.TextInput;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.general.subscriptions.Subscribable;
import com.patchworkgalaxy.general.subscriptions.Subscriber;
import com.patchworkgalaxy.template.types.ModelTemplate;

public class ModelComponentDescriptor extends StandardComponentDescriptor implements ControlFactory {
    
    private final Model _model;
    
    public ModelComponentDescriptor(ModelTemplate modelTemplate) {
	this(modelTemplate.instantiate());
    }
    
    public ModelComponentDescriptor(Model model) {
	super(null, Vector2f.ZERO, Vector2f.ZERO);
	_model = model;
    }
    
    @Override public void onShow(final Component component) {
	_model.addSubscription(ModelSubscriptionType.CLICKED,
		new Subscriber<Model>() {
		    @Override public void update(Subscribable<? extends Model> topic, Model message) {
			component.update(_model.getSubscribable(ModelSubscriptionType.CLICKED), _model);
		    }
		}
	);
	_model.setVisible(true);
    }
    
    @Override public void onHide(Component component) {
	_model.setVisible(false);
    }
    
    public Model getModel() {
	return _model;
    }

    @Override public UX2DControl getControl(UX2D ux2d, TextInput.Factory input, ControlState defaultState, String key) {
	return new UX2DControl(ux2d, input, defaultState, key) {
	    @Override protected boolean checkMousePos(Vector2f mousePos) {
		return Collisions.isMouseOver(mousePos, _model.getSpatial());
	    }
	};
    }
    
}
