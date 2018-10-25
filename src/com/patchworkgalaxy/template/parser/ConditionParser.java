package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ConditionTemplate;
import java.io.BufferedReader;

class ConditionParser extends Parser<ConditionTemplate> {

    ConditionParser(TemplateRegistry<ConditionTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Condition", NO_OP);
	require(props, "Name", STRING);
	allow(props, "Duration", INTEGER);
	allow(props, "Flags", INTEGER);
	allow(props, "TurnStart", STRING);
	allow(props, "TurnEnd", STRING);
	allow(props, "Outgoing!", MARKER);
	allow(props, "Trigger", INTEGER);
	allow(props, "Formula", STRING);
	allow(props, "Magnitude", STRING);
	allow(props, "Success", STRING);
	allow(props, "Reaction", STRING);
	allow(props, "ReactTarget", STRING);
	allow(props, "Global!", MARKER);
	props.set("Subtype", "condition:" + props.getString("Name"));
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<ConditionTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new ConditionTemplate(props));
    }
    
}
