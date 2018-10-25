package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import com.patchworkgalaxy.template.types.EventTemplate;
import com.patchworkgalaxy.template.types.WeaponTemplate;
import java.io.BufferedReader;

class WeaponParser extends Parser<WeaponTemplate> {

    WeaponParser(TemplateRegistry<WeaponTemplate> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Weapon", NO_OP);
	require(props, "Name", STRING);
	require(props, "ShortName", STRING);
	allow(props, "Flags", INTEGER);
	require(props, "Model", STRING);
	allow(props, "Magnitude", STRING);
	allow(props, "HitChance", STRING);
	allow(props, "Impact", STRING);
	
	String impact = props.getString("Impact");	
	if(impact == null) {
	    impact = new StringBuilder("Inline impact event for ").append(props.getString("Name")).toString();
	    
	    GameProps impactProps = new MutableGameProps()
		.set("Name", impact)
		.set("Type", "ModifyVitalEvent")
		.set("Subtype", "~target:vitality");
	    
	    TemplateRegistry.EVENTS.register(impact, new EventTemplate(impactProps));
	    
	    props.set("Impact", "event:" + impact);
	}
	
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<WeaponTemplate> registry, GameProps props) throws ParseException {
	String name = props.getString("Name");	
	registry.register(name, new WeaponTemplate(props));
    }
    
}
