package com.patchworkgalaxy.game.state.evolution;


public final class Coordinate {
    
    final int x, y;
    
    Coordinate(int x, int y) {
	this.x = x;
	this.y = y;
    }
    
    Coordinate add(int dx, int dy) {
	return new Coordinate(x + dx, y + dy);
    }
    
    @Override
    public int hashCode() {
	return x ^ y;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Coordinate other = (Coordinate) obj;
	if (this.x != other.x) {
	    return false;
	}
	if (this.y != other.y) {
	    return false;
	}
	return true;
    }
    
}
