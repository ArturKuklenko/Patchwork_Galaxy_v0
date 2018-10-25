package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.SpewerTemplate;
import java.io.BufferedReader;

class SpewerParser extends Parser<SpewerTemplate> {

    SpewerParser(TemplateRegistry<SpewerTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Spewer", NO_OP);
	require(props, "Name", STRING);
	require(props, "Texture", STRING);
	require(props, "Speed", FLOAT);
	require(props, "Rate", INTEGER);
	allow(props, "Direction", VECTOR3F);
	allow(props, "DirectionVariance", FLOAT);
	allow(props, "FaceVel!", MARKER);
	require(props, "LowDuration", FLOAT);
	require(props, "HighDuration", FLOAT);
	require(props, "StartColor", COLOR);
	require(props, "StartSize", FLOAT);
	require(props, "EndColor", COLOR);
	require(props, "EndSize", FLOAT);
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<SpewerTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new SpewerTemplate(props));
    }
    
}
