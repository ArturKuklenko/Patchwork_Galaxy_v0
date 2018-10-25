package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.EventTemplate;
import java.io.BufferedReader;

class EventParser extends Parser<EventTemplate> {

    EventParser(TemplateRegistry<EventTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Event", NO_OP);
	require(props, "Name", STRING);
	require(props, "Type", STRING);
	allow(props, "Subtype", STRING);
	allow(props, "Flags", INTEGER);
	allow(props, "Priority", INTEGER);
	allow(props, "Magnitude", STRING);
	allow(props, "HitChance", STRING);
	repeat(props, "Multi");
	allow(props, "Reflexive!", MARKER);
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<EventTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new EventTemplate(props));
    }
    
}
