package com.patchworkgalaxy.general.util;

import java.util.Random;

public class XORShiftRandom extends Random {
    
    private static final long serialVersionUID = 1L;
    
    private long _x;
    
    public XORShiftRandom(long seed) {
	_x = seed;
    }
    
    public XORShiftRandom() {
	this(0xDEADBEEF);
    }
    
    @Override
    public long nextLong() {
	_x ^= (_x << 21);
	_x ^= (_x >>> 35);
	_x ^= (_x << 4);
	return _x;
    }
    
    @Override
    protected int next(int bits) {
	return (int)(nextLong() >>> (64 - bits));
    }
    
    @Override
    public void setSeed(long seed) {
	if(seed != 0)
	    _x = seed;
    }
    
}
