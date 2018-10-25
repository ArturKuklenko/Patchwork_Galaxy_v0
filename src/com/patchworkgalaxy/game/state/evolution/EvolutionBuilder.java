package com.patchworkgalaxy.game.state.evolution;

import com.patchworkgalaxy.game.component.Ship;
import com.patchworkgalaxy.game.component.ShipSystem;
import com.patchworkgalaxy.game.tile.Tile;
import com.patchworkgalaxy.template.TemplateRegistry;

public class EvolutionBuilder {
    
    private final byte _rawOpcode;
    private final byte _playerId;
    private short _rngUpdate;
    private int _args;
    
    public EvolutionBuilder(Opcode opcode, byte playerId) {
	_rawOpcode = (byte)opcode.ordinal();
	_playerId = playerId;
    }
    
    public EvolutionBuilder(byte rawOpcode, byte playerId) {
	_rawOpcode = rawOpcode;
	_playerId = playerId;
    }
    
    public EvolutionBuilder(ShipSystem fireSystem) {
	_rawOpcode = (byte)(fireSystem.getTemplateId() | (~0x7F));
	_playerId = fireSystem.getPlayer().getId();
    }
    
    public EvolutionBuilder setRngUpdate(short rngUpdate) {
	_rngUpdate = rngUpdate;
	return this;
    }
    
    public EvolutionBuilder setArgs(int args) {
	_args = args;
	return this;
    }
    
    public EvolutionBuilder shift(byte in) {
	_args = (_args << 8) | in;
	return this;
    }
    
    public EvolutionBuilder shift(short in) {
	_args = (_args << 16) | in;
	return this;
    }
    
    public EvolutionBuilder shift(Coordinate in) {
	shift((byte)in.x);
	shift((byte)in.y);
	return this;
    }
    
    public EvolutionBuilder shift(Tile in) {
	shift((byte)in.x);
	shift((byte)in.y);
	return this;
    }
    
    public EvolutionBuilder shift(Ship in) {
	shift(in.getId());
	return this;
    }
    
    public EvolutionBuilder shift(TemplateRegistry tr, String name) {
	return shift((short)tr.getId(name));
    }
    
    public Evolution getEvolution() {
	return new Evolution(_rawOpcode, _playerId, _rngUpdate, _args);
    }
    
}
