package com.patchworkgalaxy.template;

import com.patchworkgalaxy.display.models.AnimationDef;
import com.patchworkgalaxy.game.misc.Faction;
import com.patchworkgalaxy.game.misc.Formula;
import com.patchworkgalaxy.game.misc.Tech;
import com.patchworkgalaxy.general.data.Namespace;
import com.patchworkgalaxy.template.parser.DataSourcesParser;
import com.patchworkgalaxy.template.types.ConditionTemplate;
import com.patchworkgalaxy.template.types.EventTemplate;
import com.patchworkgalaxy.template.types.ModelTemplate;
import com.patchworkgalaxy.template.types.ShipSystemTemplate;
import com.patchworkgalaxy.template.types.ShipTemplate;
import com.patchworkgalaxy.template.types.SpewerTemplate;
import com.patchworkgalaxy.template.types.WeaponTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemplateRegistry<T> implements Namespace {
    
    private final List<T> registryList;
    private final Map<String, T> registryMap;
    private final Map<String, Integer> idMap;
    
    private final Class<T> _type;
    
    private TemplateRegistry(Class<T> type) {
        registryList = new ArrayList<>();
        registryMap = new HashMap<>();
	_type = type;
	idMap = new HashMap<>();
    }
    
    public void register(String key, T value) {
        registryList.add(value);
        if(key != null) {
	    int id = registryList.size() - 1;
            registryMap.put(key, value);
	    idMap.put(key, id);
	    if(value instanceof Template) {
		((Template)value).setId(id);
	    }
	}
    }
    
    public T lookup(int index) {
	if(index < 0 || index > registryList.size())
	    return null;
	return registryList.get(index);
    }
    
    @Override    
    public T lookup(String key) {
	try {
	    return lookup(Integer.parseInt(key));
	}
	catch(NumberFormatException e) {
	    return registryMap.get(key);
	}
    }
    
    public int getId(String key) {
	return idMap.get(key);
    }
    
    public static Object resolveTemplate(String path) {
	
	TemplateRegistry registry = null;
	String[] split = path.split(":", 2);
	
	switch(split[0]) {
	case "ship":
	    registry = SHIPS;
	    break;
	case "weapon":
	    registry = WEAPONS;
	    break;
	case "system":
	    registry = SYSTEMS;
	    break;
	case "event":
	    registry = EVENTS;
	    break;
	case "condition":
	    registry = CONDITIONS;
	    break;
	case "formula":
	    registry = FORMULAE;
	    break;
	case "tech":
	    registry = TECHS;
	    break;
	case "faction":
	    registry = FACTIONS;
	    break;
	case "spewer":
	    registry = SPEWERS;
	    break;
	case "animation":
	    registry = ANIMATIONS;
	    break;
	case "model":
	    registry = MODELS;
	    break;
	default:
	    throw new IllegalArgumentException();
	}
	
	return registry.lookup(split[1]);
	
    }
    
    public int getRegistrySize() {
        return registryList.size();
    }
    
    public Class<T> getType() {
	return _type;
    }
    
    public static final TemplateRegistry<ShipTemplate> SHIPS = new TemplateRegistry<>(ShipTemplate.class);
    public static final TemplateRegistry<WeaponTemplate> WEAPONS = new TemplateRegistry<>(WeaponTemplate.class);
    public static final TemplateRegistry<ShipSystemTemplate> SYSTEMS = new TemplateRegistry<>(ShipSystemTemplate.class);
    public static final TemplateRegistry<EventTemplate> EVENTS = new TemplateRegistry<>(EventTemplate.class);
    public static final TemplateRegistry<ConditionTemplate> CONDITIONS = new TemplateRegistry<>(ConditionTemplate.class);
    public static final TemplateRegistry<Formula> FORMULAE = new TemplateRegistry<>(Formula.class);
    public static final TemplateRegistry<Tech> TECHS = new TemplateRegistry<>(Tech.class);
    public static final TemplateRegistry<Faction> FACTIONS = new TemplateRegistry<>(Faction.class);
    public static final TemplateRegistry<SpewerTemplate> SPEWERS = new TemplateRegistry<>(SpewerTemplate.class);
    public static final TemplateRegistry<AnimationDef> ANIMATIONS = new TemplateRegistry<>(AnimationDef.class);
    public static final TemplateRegistry<ModelTemplate> MODELS = new TemplateRegistry<>(ModelTemplate.class);
    
    
    private static boolean _initialized;
    public static void init() {
	if(_initialized) return;
        try {
            DataSourcesParser.getDefault().parse();
	    _initialized = true;
        }
        catch(ParseException e) {
	    throw new RuntimeException(e);
        }
    }
    
}
