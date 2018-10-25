package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.game.misc.Tech;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.io.BufferedReader;

public class ResearchParser extends Parser<Tech> {

    ResearchParser(TemplateRegistry<Tech> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }

    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Research", NO_OP);
	require(props, "Name", STRING);
	allow(props, "Description", STRING);
	repeat(props, "ButtonPos");
	require(props, "Icon", STRING);
	require(props, "Cost", INTEGER);
	allow(props, "Levels", INTEGER);
	repeat(props, "Prereq");
	allow(props, "Initial", STRING);
	allow(props, "PerShip", STRING);
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<Tech> registry, GameProps props) throws ParseException {
	registry.register(props.getString("Name"), new Tech(props));
    }
    
}
