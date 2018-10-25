package com.patchworkgalaxy.template.types;

import com.patchworkgalaxy.display.models.Model;
import com.patchworkgalaxy.display.models.ModelFactory;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.template.Template;

public class ModelTemplate extends Template<Model> {

    public ModelTemplate(GameProps props) {
	super(props, "model");
    }
    
    @Override
    public Model instantiate(Object... params) {
	return ModelFactory.getStandard().getModel(props);
    }
    
}
