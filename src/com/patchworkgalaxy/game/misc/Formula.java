package com.patchworkgalaxy.game.misc;

import com.patchworkgalaxy.game.state.GameState;
import com.patchworkgalaxy.general.data.GameProps;
import com.patchworkgalaxy.general.data.Numeric;
import com.patchworkgalaxy.general.data.Resolver;

public class Formula implements Numeric {
    
    private final Resolver a, b;
    private final Comparison _comparison;
    private final String _name;

    public static final Resolver KINETIC_HIT_CHANCE = new Resolver(null, "formula:Kinetic Hit Chance", true);
    public static final Resolver KINETIC_MIRACLE_CHANCE = new Resolver(null, "formula:Kinetic Miracle Chance", true);
    
    private Formula(GameProps props, Comparison comparison) {
	_name = props.getString("Name");
	a = Resolver.createResolver(null, props.getString("A"), false);
	b = Resolver.createResolver(null, props.getString("B"), false);
	_comparison = comparison;
    }
    
    private static Comparison getComparison(String name) {
	switch (name) {
	case ">":
	case "GreaterThan":
	    return GREATER_THAN;
	case ">=":
	case "GreaterThanOrEqual":
	    return GREATER_THAN_OR_EQUAL;
	case "<":
	case "LessThan":
	    return LESS_THAN;
	case "<=":
	case "LessThanOrEqual":
	    return LESS_THAN_OR_EQUAL;
	case "<>":
	case "!=":
	case "NotEquals":
	    return NOT_EQUALS;
	case "=":
	case "Equals":
	case "NumericEquals":
	    return NUMERIC_EQUALS;
	case "StrictEquals":
	    return STRICT_EQUALS;
	case "StrictNotEquals":
	    return STRICT_NOT_EQUALS;
	case "&&":
	case "And":
	    return AND;
	case "||":
	case "Or":
	    return OR;
	case "Add":
	    return ADD;
	case "Subtract":
	    return SUBTRACT;
	case "Multiply":
	    return MULTIPLY;
	case "Divide":
	    return DIVIDE;
	case "Min":
	    return MIN;
	case "Max":
	    return MAX;
	default:
	    throw new IllegalArgumentException("Unknown comparison " + name);
	}
    }
    
    public static Formula create(GameProps props) {
	Comparison c = getComparison(props.getString("Compare"));
	return new Formula(props, c);
    }
    
    public String getName() {
	return _name;
    }
    
    public boolean check(GameState gameState) {
	return objectToBoolean(gameState, toFloat(gameState));
    }
    
    static boolean objectToBoolean(GameState gameState, Object o) {
	return Float.compare(objectToFloat(gameState, o), 0) != 0;
    }
    
    static float objectToFloat(GameState gameState, Object o) {
	if(o == null)
	    return 0;
	if(o instanceof Number) {
	    return ((Number)o).floatValue();
	}
	if(o instanceof Numeric) {
	    return ((Numeric)o).toFloat(gameState);
	}
	return 1;	
    }

    @Override
    public float toFloat(GameState gameState) {
	return _comparison.compare(gameState, a, b);
    }
    
    public static interface Comparison {
	float compare(GameState gameState, Resolver a, Resolver b);
    }
    
    static final Comparison NUMERIC_EQUALS = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    Object o1 = a.resolve(gameState);
	    Object o2 = b.resolve(gameState);
	    if(o1 == null || o2 == null)
		return 0;
	    if((o1 instanceof Number || o1 instanceof Numeric) && (o2 instanceof Number || o2 instanceof Numeric)) {
		float n1 = (o1 instanceof Number ? ((Number)o1).floatValue() : ((Numeric)o1).toFloat(gameState));
		float n2 = (o2 instanceof Number ? ((Number)o2).floatValue() : ((Numeric)o2).toFloat(gameState));
		return (Float.compare(n1, n2) == 0) ? 1 : 0;
	    }
	    return o1.equals(o2) ? 1 : 0;
	}
    };
    
    static final Comparison STRICT_EQUALS = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    Object o1 = a.resolve(gameState);
	    Object o2 = b.resolve(gameState);
	    if(o1 == null)
		return 0;
	    return o1.equals(o2) ? 1 : 0;
	}
    };
    
    static final Comparison LESS_THAN = new Comparison() {

	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    Object o1 = a.resolve(gameState);
	    Object o2 = b.resolve(gameState);
	    if(o1 == null || o2 == null)
		return 0;
	    if((o1 instanceof Number || o1 instanceof Numeric) && (o2 instanceof Number || o2 instanceof Numeric)) {
		float n1 = (o1 instanceof Number ? ((Number)o1).floatValue() : ((Numeric)o1).toFloat(gameState));
		float n2 = (o2 instanceof Number ? ((Number)o2).floatValue() : ((Numeric)o2).toFloat(gameState));
		return n1 < n2 ? 1 : 0;
	    }
	    return 0;
	}
	
    };
    
    static final Comparison GREATER_THAN = new Comparison() {

	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    Object o1 = a.resolve(gameState);
	    Object o2 = b.resolve(gameState);
	    if(o1 == null || o2 == null)
		return 0;
	    if((o1 instanceof Number || o1 instanceof Numeric) && (o2 instanceof Number || o2 instanceof Numeric)) {
		float n1 = (o1 instanceof Number ? ((Number)o1).floatValue() : ((Numeric)o1).toFloat(gameState));
		float n2 = (o2 instanceof Number ? ((Number)o2).floatValue() : ((Numeric)o2).toFloat(gameState));
		return n1 > n2 ? 1 : 0;
	    }
	    return 0;
	}
	
    };
    
    static final Comparison NOT_EQUALS = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return 1 - NUMERIC_EQUALS.compare(gameState, a, b);
	}
    };
    
    static final Comparison STRICT_NOT_EQUALS = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return 1 - STRICT_EQUALS.compare(gameState, a, b);
	}
    };
    
    static final Comparison LESS_THAN_OR_EQUAL = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return Math.max(LESS_THAN.compare(gameState, a, b), NUMERIC_EQUALS.compare(gameState, a, b));
	}
    };
    
    static final Comparison GREATER_THAN_OR_EQUAL = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return Math.max(GREATER_THAN.compare(gameState, a, b), NUMERIC_EQUALS.compare(gameState, a, b));
	}
    };
    
    static final Comparison AND = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    Object o1 = a.resolve(gameState);
	    Object o2 = b.resolve(gameState);
	    float result = (objectToBoolean(gameState, o1) && objectToBoolean(gameState, o2)) ? 1 : 0;
	    return result;
	}
    };
    
    static final Comparison OR = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    Object o1 = a.resolve(gameState);
	    Object o2 = b.resolve(gameState);
	    float result = (objectToBoolean(gameState, o1) || objectToBoolean(gameState, o2)) ? 1 : 0;
	    return result;
	}
    };
    
    static final Comparison ADD = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return objectToFloat(gameState, a) + objectToFloat(gameState, b);
	}
    };
    
    static final Comparison SUBTRACT = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return objectToFloat(gameState, a) - objectToFloat(gameState, b);
	}
    };
    
    static final Comparison MULTIPLY = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return objectToFloat(gameState, a) * objectToFloat(gameState, b);
	}
    };
    
    static final Comparison DIVIDE = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return objectToFloat(gameState, a) / objectToFloat(gameState, b);
	}
    };
    
    static final Comparison MIN = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return Math.min(objectToFloat(gameState, a), objectToFloat(gameState, b));
	}
    };
    
    static final Comparison MAX = new Comparison() {
	@Override
	public float compare(GameState gameState, Resolver a, Resolver b) {
	    return Math.max(objectToFloat(gameState, a), objectToFloat(gameState, b));
	}
    };
    
}
