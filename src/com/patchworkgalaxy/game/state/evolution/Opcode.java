package com.patchworkgalaxy.game.state.evolution;

public enum Opcode {
    
    NO_OP(),
    SELECT(),
    DESELECT(),
    MAKETILES(),
    MAKESHIPS(),
    MAKEGROUP(),
    REMOVE(),
    DOEVENT(),
    BUILD(),
    RESEARCH(),
    PATCH(),
    ENDTURN(),
    DEFEAT(),
    CONCEDE(),
    SPECIAL(),
    
    ;
    
    public boolean isDiscrete() {
	return this == BUILD;
    }
    
}
