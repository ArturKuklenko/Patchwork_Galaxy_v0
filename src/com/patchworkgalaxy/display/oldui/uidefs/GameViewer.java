package com.patchworkgalaxy.display.oldui.uidefs;

import com.jme3.math.Vector2f;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.TextInput;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.display.oldui.VirtualKeyboard;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.defs.game.GameUIPD;
import com.patchworkgalaxy.game.state.GameState;

public class GameViewer {
    
    private GameViewer() {}
    
    private static final UX2D ui = UX2D.getInstance();
    //private static GameState _game;
    
    private static final UICallback SEND_CHAT_MESSAGE = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    ClientManager.client().sendChatMessage(control.getInputText());
	    control.blur();
	}
    };
    
    public static void gameViewer(GameState game) {
	//_game = game;
	ui.killAllControls();
	UI.Instance.hideAllPanels();
	UI.Instance.showPanel(new GameUIPD(game));
	initChat();
	//initCancelButton();
	//initNotification();
    }
    
    /*private static void initCancelButton() {	
	ui.createControl(
	    "Cancel",
	    new ControlState()
		.setCenter(new Vector2f(.9625f, -.8925f))
		.setDimensions(new Vector2f(.0375f, .1075f))
		.setZIndex(Definitions.Z_INDEX_ULTRAHIGH)
		.setBackground("Interface/icon/bigcancel.png")
		.setHotkey(Definitions.KEY_ESC)
		.setTooltipText(new ColoredText("Cancel\nHotkey: ").addText("[ESC]", ColorRGBA.Yellow))
	);
    }
    
    private static void initNotification() {
	ui.createControl(
	    "Notification",
	    new ControlState()
		.setCenter(new Vector2f(0f, -.71f))
		.setDimensions(new Vector2f(.5f, .023f))
		.setZIndex(520)
		.setTextAlign(BitmapFont.Align.Center)
	).setCallback(
	    CallbackType.OBSERVE,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    control.setText(GameAppState.getInstance().getNotification());
		}
	    }
	).observe(GameAppState.getSelection());
    }*/
    
    private static void initChat() {
	
	ui.createControl(
	    "Game Chat Input",
	    new TextInput.Factory("", "> ", true, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, -.95f), .5f)
		.setZIndex(500)
		.setHotkey(VirtualKeyboard.NEWLINE)
	).setCallback(
	    CallbackType.SUBMIT,
	    SEND_CHAT_MESSAGE
	);
	
	ui.createControl(
	    "Game Chat Log",
	    new ControlState()
		.setCenter(new Vector2f(0f, -.53f))
		.setDimensions(new Vector2f(.5f, .4f))
		.setZIndex(6000)
	).attachChatLog(10, 10);
    }
    
}
