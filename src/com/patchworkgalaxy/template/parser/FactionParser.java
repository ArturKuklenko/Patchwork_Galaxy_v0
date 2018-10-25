package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import com.patchworkgalaxy.game.misc.Faction;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.io.BufferedReader;

public class FactionParser extends Parser<Faction> {

    FactionParser(TemplateRegistry<Faction> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }

    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Faction", NO_OP);
	require(props, "Name", STRING);
	repeat(props, "Ship");
	repeat(props, "Tech");
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<Faction> registry, GameProps props) throws ParseException {
	registry.register(props.getString("Name"), new Faction(props));
    }
    
}
