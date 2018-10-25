package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import com.patchworkgalaxy.game.misc.Formula;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.io.BufferedReader;

public class FormulaParser extends Parser<Formula> {

    FormulaParser(TemplateRegistry<Formula> registry, BufferedReader reader, String filename) {
	super(registry, reader, filename);
    }

    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
	require(props, "@Formula", NO_OP);
	require(props, "Name", STRING);
	require(props, "Compare", STRING);
	require(props, "A", STRING);
	require(props, "B", STRING);
	return props;
    }

    @Override
    protected void interpret(TemplateRegistry<Formula> registry, GameProps props) throws ParseException {
	registry.register(props.getString("Name"), Formula.create(props));
    }
    
}
