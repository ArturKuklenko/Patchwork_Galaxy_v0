package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.display.models.AnimationDef;
import com.patchworkgalaxy.display.models.AnimationFactory;
import com.patchworkgalaxy.display.models.FindAnimation;
import com.patchworkgalaxy.display.models.Stage;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

class AnimationParser extends Parser<AnimationDef> {

    AnimationParser(TemplateRegistry<AnimationDef> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }

    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Animation", NO_OP);
	require(props, "Name", STRING);
	String type = require(props, "Type", STRING);
	allow(props, "Trigger", STRING);
	allow(props, "Mode", STRING);
	allow(props, "Point", STRING);
	allow(props, "Curve", STRING);
	allow(props, "Scale", FLOAT);
	switch(type) {
	case "Staged":
	    staged(props);
	    break;
	case "Spewer":
	    spewer(props);
	    break;
	case "Support":
	    support(props);
	    break;
	case "PatchIn":
	    patchIn(props);
	    break;
	case "Tint":
	    tint(props);
	    break;
	default:
	    throw new ParseException("Unparseable animation type " + type);
	}
	
	return props;
    }
    
    private void support(MutableGameProps props) throws ParseException {
	List<AnimationDef> list = new ArrayList<>();
	String animation = require(props, "Primary", STRING);
	list.add(FindAnimation.find(animation));
	while((animation = allow(props, "Secondary", STRING)) != null)
	    list.add(FindAnimation.find(animation));
	props.set("Animation", list);
    }
    
    private void spewer(MutableGameProps props) throws ParseException { 
	String spewer = require(props, "Spew", STRING);
	props.set("Spewer", TemplateRegistry.SPEWERS.lookup(spewer));
    }
    
    private void staged(MutableGameProps props) throws ParseException {
	allow(props, "Delay", FLOAT);
	allow(props, "DelayVariance", FLOAT);
	StageParser sp = subparser(StageParser.class, props.getString("Point"));
	List<Stage> stages = sp.stages;
	props.set("Stages", stages);
    }
    
    private void patchIn(MutableGameProps props) throws ParseException {
	//nothing to do atm
    }
    
    private void tint(MutableGameProps props)  throws ParseException {
	require(props, "Color", COLOR);
	allow(props, "Immediate!", MARKER);
    }

    @Override protected void interpret(TemplateRegistry<AnimationDef> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new AnimationFactory(props));
    }
    
}
