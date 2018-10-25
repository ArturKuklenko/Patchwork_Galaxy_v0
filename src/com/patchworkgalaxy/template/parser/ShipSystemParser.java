package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.ShipSystemTemplate;
import java.io.BufferedReader;

class ShipSystemParser extends Parser<ShipSystemTemplate> {

    ShipSystemParser(TemplateRegistry<ShipSystemTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@System", NO_OP);
	require(props, "Name", STRING);
	allow(props, "ShortName", STRING);
	allow(props, "Special!", MARKER);
	require(props, "LaunchEvent", STRING);
	allow(props, "ThermalCost", INTEGER);
	require(props, "TargetValidator", STRING);
	allow(props, "Hotkey", STRING);
	allow(props, "Icon", STRING);
	allow(props, "Description", STRING);
	allow(props, "Reusable", INTEGER);
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<ShipSystemTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");
	registry.register(name, new ShipSystemTemplate(props));
    }
    
}
