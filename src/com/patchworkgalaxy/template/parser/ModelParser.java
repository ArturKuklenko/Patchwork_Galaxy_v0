package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.display.models.AnimationDef;
import com.patchworkgalaxy.display.models.FindAnimation;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ModelTemplate;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

class ModelParser extends Parser<ModelTemplate> {

    ModelParser(TemplateRegistry<ModelTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Model", NO_OP);
	require(props, "Name", STRING);
	require(props, "File", STRING);
	allow(props, "Scale", FLOAT);
	allow(props, "Speed", FLOAT);
	repeat(props, "Point");
	
	List<AnimationDef> animations = new ArrayList<>();
	String tmp;
	while((tmp = allow(props, "Animation", STRING)) != null) {
	    AnimationDef animation = FindAnimation.find(tmp);
	    if(animation != null) {
		animations.add(animation);
	    }
	}
	props.set("Animation", animations);
	
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<ModelTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new ModelTemplate(props));
    }
    
}
