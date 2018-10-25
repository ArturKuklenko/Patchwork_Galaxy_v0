package com.patchworkgalaxy.game.state.evolution;

import com.jme3.network.Message;
import com.jme3.network.serializing.Serializable;

@Serializable
public class Evolution implements Message {
    
    private byte _opcode, _playerId;
    private short _seedUpdate;
    private int _args;
    private boolean _validated;
    
    /**
     * Serialization only. Marked as deprecated so people get a warning if they
     * mistakenly use it.
     * @deprecated
     */
    @Deprecated
    public Evolution() {}
    
    public Evolution(byte rawOpcode, byte playerId, short seedUpdate, int args) {
	_opcode = rawOpcode;
	_playerId = playerId;
	_seedUpdate = seedUpdate;
	_args = args;
    }
    
    private Evolution(Evolution copyOf) {
	_opcode = copyOf._opcode;
	_playerId = copyOf._playerId;
	_seedUpdate = copyOf._seedUpdate;
	_args = copyOf._args;
	_validated = copyOf._validated;
    }
    
    byte getRawOpcode() {
	return _opcode;
    }
    
    Opcode getOpcode() {
	if(_opcode < 0)
	    return null;
	return Opcode.values()[_opcode];
    }
    
    byte getPlayerId() {
	return _playerId;
    }
    
    long getSeedUpdate() {
	long update = _seedUpdate;
	update |= (update << 16);
	update |= (update << 32);
	return update;
    }
    
    int getArgs() {
	return _args;
    }
   
    byte[] getArgBytes() {
	return new byte[] {
		(byte)(_args >>> 24),
		(byte)(_args >>> 16),
		(byte)(_args >>> 8),
		(byte)(_args)
	};
    }
    
    short[] getArgShorts() {
	return new short[] {
	    (short)(_args >>> 16),
	    (short)(_args)
	};
    }

    /**
     * @return Checks if this evolution represents private information that
     * opponents shouldn't see. If true, the server will validate the evolution
     * and send it back to the sender (assuming it passed), but not notify
     * enemies.
     */
    public boolean isDiscrete() {
	Opcode opcode = getOpcode();
	return (opcode != null && opcode.isDiscrete());
    }
    
    @Override
    public Message setReliable(boolean f) {
	return this;
    }

    @Override
    public boolean isReliable() {
	return true;
    }
    
    public Evolution withValidationState(boolean validated) {
	Evolution result = new Evolution(this);
	result._validated = validated;
	return result;
    }
    
    public Evolution withRngSeed(short seedUpdate) {
	Evolution result = new Evolution(this);
	result._seedUpdate = seedUpdate;
	return result;
    }
    
    public boolean isValidated() {
	return _validated;
    }
    
}
