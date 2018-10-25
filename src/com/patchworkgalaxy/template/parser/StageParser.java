package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.display.models.AnimationDef;
import com.patchworkgalaxy.display.models.Stage;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.SpewerTemplate;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

class StageParser extends Parser<Stage> {

    final List<Stage> stages;
    private final String _point;
    
    StageParser(BufferedReader reader, Object[] props) {
	super(reader);
	stages = new ArrayList<>();
	_point = (String)props[0];
    }

    @Override
    protected GameProps section() throws ParseException {
	GameProps props = new MutableGameProps();
	if(allow(props, "@Stage", NO_OP) == null)
	    return null;
	require(props, "Start", FLOAT);
	require(props, "End", FLOAT);
	if(allow(props, "Scale", FLOAT) == null)
	    props.set("Scale", 1);
	float scale = props.getFloat("Scale");
	
	List<AnimationDef> anims = new ArrayList<>();
	
	String tmp;
	while((tmp = allow(props, "Invoke", STRING)) != null) {
	    AnimationDef invoke = TemplateRegistry.ANIMATIONS.lookup(tmp);
	    if(invoke != null)
		anims.add(invoke);
	}
	
	while((tmp = allow(props, "Spew", STRING)) != null) {
	    SpewerTemplate st = TemplateRegistry.SPEWERS.lookup(tmp);
	    if(st != null)
		anims.add(AnimationDef.forSpewer(st, _point, scale));
	}
	
	props.set("Anims", anims);
	
	allow(props, "Offset", VECTOR3F);
	allow(props, "Special!", MARKER);
	
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<Stage> registry, GameProps props) throws ParseException {
	stages.add(Stage.create(props));
    }
    
}
