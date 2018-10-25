package com.patchworkgalaxy.template.parser;

import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.MutableGameProps;
import com.patchworkgalaxy.template.ParseException;
import com.patchworkgalaxy.template.Parser;
import static com.patchworkgalaxy.template.ParserType.*;
import com.patchworkgalaxy.template.TemplateRegistry;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import kvp.KVPs;

public class DataSourcesParser extends Parser<Object> {
    
    public DataSourcesParser(BufferedReader reader, String filename) {
        super(null, reader, filename);
    }
    
    public static DataSourcesParser getDefault() throws ParseException {
	return new DataSourcesParser(getBufferedReader("datasources.kvp"), "datasources.kvp");
    }
    
    @Override
    protected GameProps section() throws ParseException {
	MutableGameProps props = new MutableGameProps();
        require(props, "Ships", STRING);
        require(props, "Weapons", STRING);
	require(props, "Factions", STRING);
        require(props, "Events", STRING);
        require(props, "Systems", STRING);
	require(props, "Conditions", STRING);
	require(props, "Formulas", STRING);
	require(props, "Research", STRING);
	require(props, "Models", STRING);
	require(props, "Animations", STRING);
	require(props, "Spewers", STRING);
	return props;
    }
    
    @Override
    protected void interpret(TemplateRegistry<Object> registry, GameProps props) throws ParseException {
	
	String file;
	
	file = props.getString("Formulas");
	(new FormulaParser(TemplateRegistry.FORMULAE, getBufferedReader(file), file)).parse();
	
	file = props.getString("Spewers");
	(new SpewerParser(TemplateRegistry.SPEWERS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Animations");
	(new AnimationParser(TemplateRegistry.ANIMATIONS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Models");
	(new ModelParser(TemplateRegistry.MODELS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Events");
	(new EventParser(TemplateRegistry.EVENTS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Weapons");
	(new WeaponParser(TemplateRegistry.WEAPONS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Conditions");
	(new ConditionParser(TemplateRegistry.CONDITIONS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Systems");
	(new ShipSystemParser(TemplateRegistry.SYSTEMS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Research");
	(new ResearchParser(TemplateRegistry.TECHS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Ships");
	(new ShipParser(TemplateRegistry.SHIPS, getBufferedReader(file), file)).parse();
	
	file = props.getString("Factions");
	(new FactionParser(TemplateRegistry.FACTIONS, getBufferedReader(file), file)).parse();
	
    }
    
    private static BufferedReader getBufferedReader(String filename) throws ParseException {
	try {
	    return new BufferedReader(new FileReader("data/" + filename));
	}
	catch(FileNotFoundException e) {
	    try {
		return new BufferedReader(new InputStreamReader(KVPs.class.getResourceAsStream(filename)));
	    }
	    catch(Exception e2) {
		throw new ParseException(e);
	    }
	}
    }
    
    @Override
    public void parse() throws ParseException {
	super.parse();
    }
    
}
