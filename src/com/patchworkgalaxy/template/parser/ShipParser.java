package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipTemplate;
import java.io.BufferedReader;

class ShipParser extends Parser<ShipTemplate> {

    ShipParser(TemplateRegistry<ShipTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Ship", NO_OP);
	require(props, "Name", STRING);
	allow(props, "Heroic!", MARKER);
	require(props, "Model", STRING);
	allow(props, "Speed", FLOAT);
	allow(props, "Acceleration", FLOAT);
	require(props, "BuildCost", INTEGER);
	require(props, "PatchTime", STRING);
	require(props, "Hull", INTEGER);
	require(props, "Shield", INTEGER);
	require(props, "ShieldRegen", INTEGER);
	require(props, "Thermal", INTEGER);
	require(props, "Tier", INTEGER);
	require(props, "Falloff", FLOAT);
	allow(props, "Icon", STRING);
	allow(props, "Description", STRING);
	repeat(props, "System");
	repeat(props, "Hangar");
	allow(props, "TypeA!", MARKER);
	repeat(props, "Condition");
	
	if(!props.hasKey("Speed"))
	    props.set("Speed", 30);
	
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<ShipTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new ShipTemplate(props));
    }
    
}
