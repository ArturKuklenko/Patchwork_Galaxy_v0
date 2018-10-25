package com.patchworkgalaxy.display.oldui.uidefs;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;

public class Notifier {
    
    private Notifier() {}
    
    private static UX2D ui = UX2D.getInstance();
    
    private static UICallback HIDE_NOTIFICATION = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    ui.killSection("Notification");
	}
    };
    
    public static void notify(ColoredText message) {
	ui.beginSection("Notification");
	
	ui.createControl(
	    "Notification Underlay",
	    ControlStates.INTERVENE()
		.setZIndex(990)
	).setCallback(
	    CallbackType.CLICK,
	    HIDE_NOTIFICATION
	);
	
	ui.createControl(
	    "Notification",
	    ControlStates.POPUP()
		.setZIndex(1000)
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(16)
		.setOffset(new Vector2f(0f, -.1f))
		.setText(message)
	).setCallback(
	    CallbackType.CLICK,
	    HIDE_NOTIFICATION
	);
	
	ui.createControl(
	    "Notification Button",
	    ControlStates.BUTTON(new Vector2f(0, -.2f), new Vector2f(.15f, .1f), "OK")
		.setZIndex(6000)
		.setFontSize(20)
	);
	
	ui.concludeSection();
    }
    
    public static void notify(String message) {
	notify(new ColoredText(message));
    }
    
    public static void errorNotify(String message) {
	notify(new ColoredText().addText(message, ColorRGBA.Red));
    }
    
    public static void connectionNotify(boolean on) {
	if(!on) {
	    ui.killSection("Connection Notification");
	    return;
	}
	
	ui.beginSection("Connection Notification");
	
	ui.createControl(
	    "Connection Notification Underlay",
	    ControlStates.INTERVENE()
		.setZIndex(990)
	);
	
	ui.createControl(
	    "Connection Notification",
	    ControlStates.POPUP()
		.setZIndex(1000)
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(16)
		.setOffset(new Vector2f(0f, -.1f))
		.setText("Working")
	).setCallback(
	    CallbackType.TICK,
	    new UICallback() {
		float timer;
		int dots;
		@Override
		public void callback(UX2DControl control) {
		    timer += control.getTPF();
		    if(timer > dots) {
			dots = (dots + 1) % 5;
			StringBuilder sb = new StringBuilder("Working");
			for(int i = 0; i < dots; ++i)
			    sb.append(" .");
			control.setText(sb.toString());
		    }
		}
	    }
	);
	
	ui.concludeSection();
    }
    
    public static void smallNotify(ColoredText message) {
	smallNotify(message, new Vector2f(0f, 0f));
    }
    
    public static void smallNotify(ColoredText message, Vector2f where) {
	ui.createControl(
	    "Small Notification",
	    new ControlState()
		.setCenter(where)
		.setDimensions(new Vector2f(1f, .23f))
		.setTextAlign(BitmapFont.Align.Center)
		.setZIndex(6000)
		.setText(message)
	).setCallback(
	    CallbackType.TICK,
	    new UICallback() {
		float duration = 5f;
		@Override
		public void callback(UX2DControl control) {
		    duration -= control.getTPF();
		    if(duration <= 0)
			control.changeState(new ControlState().invalidate());
		}
	    }
	);
    }
    
    public static void smallNotify(String message, Vector2f where) {
	smallNotify(new ColoredText(message), where);
    }
    
}
