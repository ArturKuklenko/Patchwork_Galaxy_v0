package com.patchworkgalaxy.general.util;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializer;
import com.patchworkgalaxy.game.state.GameHistory;
import com.patchworkgalaxy.game.state.evolution.Evolution;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.network.transaction.NetTransaction;
import com.patchworkgalaxy.network.transaction.Response;
import com.patchworkgalaxy.udat.ChannelData;
import com.patchworkgalaxy.udat.UserData;
import java.util.List;

public class Utils {
    
    private Utils() {}

    /**
     * Appends null to a list until it is at least the requested length. The
     * return value indicates if any appending was necessary: if it's false,
     * the list was already long enough, possibly longer; if it's true, one or
     * more nulls had to be added, and the list is now exactly the requested
     * length.
     * @param l the list to append to
     * @param len the minumum length
     * @return whether any nulls had to be appended
     */
    public static boolean rightpad(List<?> l, int len) {
	int delta = (len - l.size()) + 1;
	if(delta > 0) {
	    while (--delta >= 0) {
		l.add(null);
	    }
	    return true;
	}
	return false;
    }
    
    public static ColorRGBA parseColor(String string) {
	if(string == null)
	    throw new IllegalArgumentException("Error parsing color: null input");
	int len = string.length();
	if(len != 3 && len != 6 && len != 8)
	    throw new IllegalArgumentException("Error parsing color " + string + ": expected a 3, 6, or 8-character hexidecimal number");
	int step = (len == 3 ? 1 : 2);
	try {
	    int rx = Integer.valueOf(string.substring(0, step), 16);
	    int gx = Integer.valueOf(string.substring(step, 2 * step), 16);
	    int bx = Integer.valueOf(string.substring(2 * step, 3 * step), 16);
	    int ax;
	    if(len == 8)
		ax = Integer.valueOf(string.substring(6, 8), 16);
	    else
		ax = 255;
	    float r = ((float)rx)/255f;
	    float g = ((float)gx)/255f;
	    float b = ((float)bx)/255f;
	    float a = ((float)ax)/255f;
	    return new ColorRGBA(r, g, b, a);
	}
	catch(NumberFormatException e) {
	    throw new IllegalArgumentException("Error parsing color" + string + ": expected a 3, 6, or 8-character hexidecimal number");
	}
    }
    
    public static Vector3f parseVec(String vec) {
	
	if(vec == null)
	    throw new IllegalArgumentException("Error parsing vector: null input");
	
	String[] avec = vec.split(",");
	if(avec.length != 3)
	    throw new IllegalArgumentException("Error parsing vector " + vec + ": expected three comma-separated values");
	
	return parseVec(avec);
	
    }
    
    public static Vector3f parseVec(String[] vec) {
	
	if(vec == null)
	    throw new IllegalArgumentException("Error parsing vector: null input");
	
	if(vec.length != 3)
	    throw new IllegalArgumentException("Error parsing vector " + vec + ": expected three comma-separated values");
	
	float[] fvec = new float[3];
	
	try {
	    for(int i = 0; i < 3; ++i) {
		fvec[i] = Float.valueOf(vec[i].trim());
	    }
	    return new Vector3f(fvec[0], fvec[1], fvec[2]);
	}
	catch(NumberFormatException e) {
	    throw new IllegalArgumentException("Error parsing vector " + vec + ": " + e.getLocalizedMessage());
	}
	
    }
    
    public static boolean areEqual(Object o1, Object o2) {
	if(o1 == null)
	    return o2 == null;
	return o1.equals(o2);
    }
    
    public static float randUnitShifted() {
	return (float)Math.random() - .5f;
    }
    
    public static void initSerialization() {
        Serializer.registerClass(GameHistory.class);
        Serializer.registerClass(ProtocolMessage.class);
        Serializer.registerClass(ChatMessage.class);
        Serializer.registerClass(Evolution.class);
	Serializer.registerClass(UserData.class);
	Serializer.registerClass(ChannelData.class);
	Serializer.registerClass(NetTransaction.class);
	Serializer.registerClass(Response.class);
    }
    
    public static Vector2f averageVecs(Vector2f... vecs) {
	Vector2f result = new Vector2f();
	for(Vector2f vec : vecs)
	    result.addLocal(vec);
	return result.divideLocal(vecs.length);
    }
    
}
