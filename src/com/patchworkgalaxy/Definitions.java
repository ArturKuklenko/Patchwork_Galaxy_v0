package com.patchworkgalaxy;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class Definitions {
    private Definitions() {}
    
    public static final int
	    DEFAULT_RESOLUTION_X = 1422,
	    DEFAULT_RESOLUTION_Y = 800,
	    DEFAULT_FONT_SIZE = 12
	    ;
    
    public static final float
	    SMALL_RESOLUTION_SCALE = .8f,
	    LARGE_RESOLUTION_SCALE = 1.5f
	    ;
    
    public static final float
	    COMMAND_CARD_BUTTON_WIDTH = 25f / DEFAULT_RESOLUTION_X,
	    COMMAND_CARD_BUTTON_HEIGHT = 25f / DEFAULT_RESOLUTION_Y,
	    COMMAND_CARD_BUTTON_PADDING_X = 2f / DEFAULT_RESOLUTION_X,
	    COMMAND_CARD_BUTTON_PADDING_Y = 2f / DEFAULT_RESOLUTION_Y,
	    THERMAL_BLOCK_WIDTH = 14f / DEFAULT_RESOLUTION_X,
	    THERMAL_BLOCK_HEIGHT = 14f / DEFAULT_RESOLUTION_Y,
	    THERMAL_BLOCK_PADDING_X = 2f / DEFAULT_RESOLUTION_X,
	    THERMAL_BLOCK_PADDING_Y = 2f / DEFAULT_RESOLUTION_Y,
	    HP_BAR_SIZE = 16,
	    HP_BAR_PADDING = 2,
	    COMMAND_CARD_OFFSET_X = 0,
	    COMMAND_CARD_OFFSET_Y = COMMAND_CARD_BUTTON_HEIGHT,
	    RESOURCES_PANEL_WIDTH = 240f / DEFAULT_RESOLUTION_X,
	    RESOURCES_PANEL_HEIGHT = 100f / DEFAULT_RESOLUTION_Y,
	    RESOURCES_PANEL_X = .2f,
	    RESOURCES_PANEL_Y = 1f - RESOURCES_PANEL_HEIGHT,
	    LOCATION_PANEL_X = RESOURCES_PANEL_X - 2 * RESOURCES_PANEL_WIDTH - .02f
	    ;
    
    public static final int
	    THERMAL_BLOCKS_PER_COLUMN = 3,
	    COMMAND_CARD_BUTTONS_PER_ROW = 3
	    ;
    
    public static final String
	    GENERIC_TB_IMAGE = "Interface/icon/tb/generic.png",
	    WEAPON_TB_IMAGE = "Interface/icon/tb/weapon.png",
	    ENGINE_TB_IMAGE = "Interface/icon/tb/engine.png",
	    HULL_BAR_IMAGE = "Interface/icon/hp/hull.png",
	    SHIELD_BAR_IMAGE = "Interface/icon/hp/shield.png",
	    COMMAND_CARD_BACKGROUND_IMAGE = "Interface/panel.png",
	    GAME_MENU_BUTTON_BACKGROUND_IMAGE = "Interface/main/widebutton_blue.png",
	    GAME_MENU_BUTTON_ALT_IMAGE = "Interface/main/widebutton_green.png",
	    GAME_MENU_BUTTON_RED_IMAGE = "Interface/main/widebutton_red.png"
	    ;
    
    public static final Vector2f
	    GAME_MENU_BUTTON_SIZE = new Vector2f(140f/DEFAULT_RESOLUTION_X, 40f/DEFAULT_RESOLUTION_Y),
	    GAME_MENU_BUTTON_ORIGIN = new Vector2f(-1 + GAME_MENU_BUTTON_SIZE.x, 1 - GAME_MENU_BUTTON_SIZE.y),
	    GAME_MENU_BUTTON_STEP = new Vector2f(0, -GAME_MENU_BUTTON_SIZE.y * 2);
    
    public static final Vector3f
	    PATCH_SPACE_ORIGIN = new Vector3f(0, 800, -900),
	    PATCH_SPACE_STEP = new Vector3f(40, 80, 0),
	    PATCH_SPACE_BUTTON_FACING = new Vector3f(0, -10, 0),
	    PATCH_SPACE_SHIP_FACING = new Vector3f(0, -10, 0)
	    ;
    
    public static final String[]
	    GAME_MENU_BUTTON_LABELS = {"End Turn", "Concede", "Chat"}
	    ;
    
    public static final float
	    Z_INDEX_BASE = 0,
	    Z_INDEX_LOW = 200,
	    Z_INDEX_MED = 400,
	    Z_INDEX_HIGH = 600,
	    Z_INDEX_ULTRAHIGH = 800
	    ;
    
    public static final char
	    KEY_DUMMY = (char)1,
	    KEY_ESC = (char)27,
	    KEY_ENTER = (char)13
	    ;
    
}
