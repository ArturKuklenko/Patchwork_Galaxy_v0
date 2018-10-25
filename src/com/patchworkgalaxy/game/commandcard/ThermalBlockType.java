package com.patchworkgalaxy.game.commandcard;

import com.patchworkgalaxy.Definitions;

public enum ThermalBlockType {
    
    GENERIC, WEAPON, ENGINE;
    
    public String getIcon() {
	switch(this) {
	case WEAPON:
	    return Definitions.WEAPON_TB_IMAGE;
	case ENGINE:
	    return Definitions.ENGINE_TB_IMAGE;
	default:
	    return Definitions.GENERIC_TB_IMAGE;
	}
    }
    
    public String getDescription() {
	switch(this) {
	case WEAPON:
	    return "A weapon-only Thermal Block";
	case ENGINE:
	    return "A movement-only Thermal Block";
	default:
	    return "A general-purpose Thermal Block";
	}
    }
    
}
