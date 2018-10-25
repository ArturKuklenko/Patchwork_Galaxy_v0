package com.patchworkgalaxy.display.oldui.uidefs;

import com.jme3.font.BitmapFont;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.ControlState;

public class ControlStates {
    
    private ControlStates() {}
    
    public static ControlState INTERVENE() {
	return new ControlState()
		.setDimensions(new Vector2f(1, 1))
		.setZIndex(250);
    }
    
    public static ControlState POPUP() {
	return new ControlState()
		.setDimensions(new Vector2f(.5f, .5f))
		.setZIndex(275)
		.setBackground("Interface/panel.png");
    }
    
    public static ControlState DIM(ControlState base, float amount) {
	return new ControlState(base)
	        .setOpacity(1f - amount);
    }
    
    public static ControlState TEXTBOX(Vector2f center, float width) {
	return new ControlState()
		.setCenter(new Vector2f(center))
		.setDimensions(new Vector2f(width, .022f))
		.setZIndex(300)
		.setBackground("Interface/offblack.png");
    }
    public static ControlState BIG_TEXTBOX(Vector2f center, float width) {
	return TEXTBOX(center, width)
		.setFontSize(16f)
		.setDimensions(new Vector2f(width, .03f));
    }    
    
    public static ControlState HUGE_TEXTBOX(Vector2f center, float width) {
	return TEXTBOX(center, width)
		.setFontSize(20f)
		.setDimensions(new Vector2f(width, .04f));
    }
    
    public static ControlState MAIN_MENU_HEADER(String label, float x) {
	return MAIN_MENU_HEADER(label, x, 0);
    }
    
    public static ControlState MAIN_MENU_HEADER(String label, float x, float yOffset) {
	return new ControlState()
		.setCenter(new Vector2f(x, .88f + yOffset))
	        .setDimensions(new Vector2f(.1f, .07f))
	        .setFontSize(24)
	        .setZIndex(10)
	        .setTextAlign(BitmapFont.Align.Center)
		.setText(label);
    }
    
    public static ControlState MAIN_MENU_HEADER_ACTIVE(String label){
	return MAIN_MENU_HEADER(label, 0)
		.setCenter(new Vector2f(0f, .62f));
    }
    
    
    public static  ControlState MAIN_MENU_OPTION_DISABLED(String label) {
	return new ControlState()
	        .setDimensions(new Vector2f(.2f, .023f))
	        .setCenter(new Vector2f(0f, .6f))
	        .setFontSize(16)
	        .setZIndex(-20)
	        .setTextAlign(BitmapFont.Align.Center)
	        .setOpacity(0f)
		.setText(label);
    }
    
    public static ControlState MAIN_MENU_OPTION_VISIBLE(String label, int index) {
	float y = .60f - (.05f * index);
	return MAIN_MENU_OPTION_DISABLED(label)
	        .setCenter(new Vector2f(0f, y))
	        .setZIndex(20)
	        .setOpacity(1)
		.setOffset(new Vector2f(0f, 0f));
    }
    
    public static ControlState MAIN_MENU_OPTION_FOCUS(String label, int index) {
	return MAIN_MENU_OPTION_VISIBLE(label, index)
		.setOpacity(.5f)
		.setOffset(new Vector2f(.01f, 0f));
    }
    
    public static ControlState PANEL(Vector2f center, Vector2f dimensions) {
	return new ControlState()
		.setCenter(new Vector2f(center))
	        .setDimensions(new Vector2f(dimensions))
	        .setBackground("Interface/panel.png")
	        .setZIndex(295)
		.setTextAlign(BitmapFont.Align.Center);	
    }
    
    public static ControlState DARKPANEL(Vector2f center, Vector2f dimensions) {
	return PANEL(center, dimensions)
	    .setBackground("Interface/offblack.png")
	    .setZIndex(297);
    }
    
    public static ControlState BUTTON(Vector2f center, Vector2f dimensions, ColoredText label) {
	return new ControlState()
		.setCenter(new Vector2f(center))
	        .setDimensions(new Vector2f(dimensions))
		.setOffset(new Vector2f(0f, -dimensions.y / 3))
	        .setBackground("Interface/button.png")
	        .setZIndex(300)
		.setText(label)
		.setTextAlign(BitmapFont.Align.Center);
    }
    
    public static ControlState BUTTON(Vector2f center, Vector2f dimensions, String label) {
	return BUTTON(center, dimensions, new ColoredText(label));
    }
    
    public static ControlState LOBBY_BUTTON(Vector2f center, String label) {
	return BUTTON(center, new Vector2f(.1f, .05f), label);
    }
    
    public static ControlState COMMAND_CARD_BUTTON(String icon, char hotkey, boolean online, int index) {
	float x = (index % 3) * .1f + .65f;
	float y = (index / 3) * -.1375f - .8f;
	return new ControlState()
	        .setDimensions(new Vector2f(.04f, 0.0625f))
	        .setCenter(new Vector2f(x, y))
	        .setBackground("Interface/" + icon)
	        .setHotkey(hotkey)
	        .setZIndex(600 - index)
		.setOpacity(online ? 1f : .5f);
    }
    
    public static ControlState SHIPYARD_BUTTON(String icon, int index) {
	float x = (index % 3) * .46f - .5f;
	float y = (index / 3) * -.6f + .65f;
	return new ControlState()
	        .setDimensions(new Vector2f(.18f, .23f))
	        .setCenter(new Vector2f(x, y))
	        .setBackground(icon)
		.setZIndex(600);
    }
    
    public static ControlState RESEARCH_BUTTON(String icon, Vector2f center, Vector2f dimensions) {
	return new ControlState()
		.setCenter(new Vector2f(center))
		.setDimensions(new Vector2f(dimensions))
		.setBackground(icon)
		.setZIndex(450);
    }
    
}
