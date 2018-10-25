package com.patchworkgalaxy.plex;

 public class Definitions {
    
    private Definitions() {}
    
    public static final String NULL_CHAR = "" + (char)0;
    public static final String SERIALIZER_DIVIDER = "\n";
    
    public static final String
	    INIT_KEYWORD = "init",
	    DISPLAY_KEYWORD = "display",
	    ANY_UPDATE_KEYWORD = "any";
    
    static final int
	    INIT_ID = -1,
	    DISPLAY_ID = -2,
	    ANY_UPDATE_ID = -3;
    
}
