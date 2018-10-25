package com.patchworkgalaxy.display.oldui.uidefs;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.TextInput;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.network.oldmessages.ProtocolCallbacks;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.udat.ChannelData;
import com.patchworkgalaxy.udat.SpecialKeys;
import com.patchworkgalaxy.udat.UserData;

public class ChatChannel {
    
    private ChatChannel() {}
    
    private static final UX2D ui = UX2D.getInstance();
    
    private static final UICallback SEND_CHAT_MESSAGE = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    String message = control.getInputText().trim();
	    if(message.length() > 0) {
		ClientManager.client().sendChatMessage(message);
		control.clearInput();
	    }
	}
    };
    
    private static final UICallback DISMISS_PROMPT = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    ui.killSection("Prompt");
	}
    };
    
    private static ColoredText observersAllowed() {
	UserData local = GameLobby.getLocalUser();
	if(local != null && local.booleanDatum(SpecialKeys.ALLOWS_SPECTATORS))
	    return new ColoredText("Observers allowed: ").addText("YES", ColorRGBA.Green);
	else
	    return new ColoredText("Observers allowed: ").addText("NO", ColorRGBA.Red);
	
    }
    
    private static final UICallback TOGGLE_ALLOW_SPECTATORS = new UICallback() {
	@Override public void callback(final UX2DControl control) {
	    boolean allow = !GameLobby.getLocalUser().booleanDatum(SpecialKeys.ALLOWS_SPECTATORS);
	    ClientManager.client().send(
		    new ProtocolMessage("UDAT" + SpecialKeys.ALLOWS_SPECTATORS + ";;" + allow),
		    new ProtocolCallbacks() {
			@Override public void succeed(ProtocolMessage result) {}
			@Override public void fail(ProtocolMessage reason) {}	
		    });
	}
    };
    
    public static void chatChannel() {
	ui.killAllControls();
	
	ui.createControl(
		"Lobby Chat",
		new ControlState()
		    .setDimensions(new Vector2f(.8f, .8f))
		    .setCenter(new Vector2f(.1f, 0f))
		    .setBackground("Interface/chat.png")		
	).attachChatLog(45);
	
	ui.createControl(
		"Lobby Chat Text",
		new TextInput.Factory("", "> ", false, false),
		ControlStates.TEXTBOX(new Vector2f(.1f, -.9f), .8f)
	).setCallback(
		CallbackType.SUBMIT,
		SEND_CHAT_MESSAGE
	).focus();
	
	initChannelMonitor();
	initButtons();
	
    }
    
    private static void initButtons() {
	
	ui.createControl(
		"Host Game",
		ControlStates.BUTTON(new Vector2f(-.85f, .36f), new Vector2f(.1f, .05f), "Host Game")
	).setCallback(
		CallbackType.CLICK,
		new UICallback() {
		    @Override
		    public void callback(UX2DControl control) {
			showChannelPrompt(true);
		    }
		}
	);
	
	ui.createControl(
		"Find Games",
		ControlStates.BUTTON(new Vector2f(-.85f, .24f), new Vector2f(.1f, .05f), "Find Games")
	).setCallback(
		CallbackType.CLICK,
		ChatChannelAux.SHOW_GAMES_LIST
	);
	
	ui.createControl(
		"Create Channel",
		ControlStates.BUTTON(new Vector2f(-.85f, .12f), new Vector2f(.1f, .05f), "Create Channel")
	).setCallback(
		CallbackType.CLICK,
		new UICallback() {
		    @Override
		    public void callback(UX2DControl control) {
			showChannelPrompt(false);
		    }
		}
	);
	
	ui.createControl(
		"List Channels",
		ControlStates.BUTTON(new Vector2f(-.85f, 0f), new Vector2f(.1f, .05f), "List Channels")
	).setCallback(
		CallbackType.CLICK,
		ChatChannelAux.SHOW_CHANNEL_LIST
	);
	
	ui.createControl(
	    "Account",
	    ControlStates.BUTTON(new Vector2f(-.85f, -.12f), new Vector2f(.1f, .05f), "Manage\naccount/contacts")
		.setOffset(new Vector2f(0f, -.005f))
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    AccountManagement.show();
		}
	    }
	);
	
	ui.createControl(
		"Disconnect",
		ControlStates.BUTTON(new Vector2f(-.85f, -.24f), new Vector2f(.1f, .05f), "Disconnect")
	).setCallback(
		CallbackType.CLICK,
		new UICallback() {
		    @Override
		    public void callback(UX2DControl control) {
			ClientManager.client().disconnect();
			MainMenu.mainMenu();
		    }
		}
	);
    }
    
    private static void initChannelMonitor() {
	ui.createControl(
		"Channel Monitor",
		ControlStates.PANEL(new Vector2f(-.85f, .63f), new Vector2f(.1f, .17f))
	).setCallback(
		CallbackType.TICK,
		new UICallback() {
		    @Override
		    public void callback(UX2DControl control) {
			ColoredText text = new ColoredText();
			ChannelData channel = ClientManager.client().getChannelData();
			text.addText("Channel " + channel.getChannelName(), ColorRGBA.Yellow);
			for(String username : channel.getUsernames())
			    text.addText("\n" + username);
			control.setText(text);
		    }
		}
	);
    }
    
    private static void showChannelPrompt(boolean game) {
	
	ui.beginSection("Prompt");
	
	final String title = game ? "Hosting game" : "Creating chat channel";
	final String label = game ? "Game name: " : "Channel name: ";
	final String make = game ? "Host game" : "Create channel";
	final String makePublic = game ? "Make game public" : "Make channel public";
	final String makePrivate = game ? "Make game private" : "Make channel private";
	
	ui.createControl(
		"Prompt Underlay",
		ControlStates.INTERVENE()
	).setCallback(CallbackType.CLICK, DISMISS_PROMPT);
	
	ui.createControl(
	    "Prompt Box",
	    ControlStates.POPUP()
	);
	
	ui.createControl(
	    "Prompt Title",
	    new ControlState()
		.setDimensions(new Vector2f(.6f, .03f))
		.setCenter(new Vector2f(0f, .3f))
		.setText(new ColoredText().addText(title, ColorRGBA.Yellow))
		.setZIndex(400)
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(16)
	);
	
	ui.createControl(
	    "Channel Name",
	    new TextInput.Factory(label, "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, .1f), .4f)
	).setCallback(
	    CallbackType.SUBMIT,
	    ChatChannelAux.getJoinCallback(game, true)
	).focus();
	
	ui.createControl(
	    "Channel Password",
	    new TextInput.Factory("Password: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(.1f, .1f), .25f).setOpacity(0)
	);
	
	ui.createControl(
	    "Channel Privacy",
	    ControlStates.TEXTBOX(new Vector2f(-.27f, .03f), .1f)
		.setText(makePrivate)
		.setTextAlign(BitmapFont.Align.Center)
	).setCallback(
	    CallbackType.CLICK,
	    Callbacks.toggle(
		ControlStates.TEXTBOX(new Vector2f(-.27f, .03f), .1f)
		    .setText(makePublic)
		    .setTextAlign(BitmapFont.Align.Center),
		.01f,
		Callbacks.stateChangerWithDuration(
		    "Channel Password",
		    ControlStates.TEXTBOX(new Vector2f(.1f, .03f), .25f).setOpacity(1),
		    1f
		),
		Callbacks.stateChangerWithDuration("Channel Password", null, 1f)
	    )
	);
	
	if(game) {
	    ui.createControl("Allow Observers",
		    ControlStates.TEXTBOX(new Vector2f(-.27f, -.02f), .1f)
			.setText(observersAllowed()))
			.setCallback(CallbackType.CLICK, TOGGLE_ALLOW_SPECTATORS)
			.setCallback(CallbackType.TICK, new UICallback() {
			    @Override public void callback(UX2DControl control) {
				control.setText(observersAllowed());
			    }
			});
	}
	
	ui.createControl(
	    "Create Channel Button",
	    ControlStates.BUTTON(new Vector2f(-.2f, -.15f), new Vector2f(.18f, .1f), make)
		    .setTextAlign(BitmapFont.Align.Center)
		    .setFontSize(20)
	).setCallback(CallbackType.CLICK, ChatChannelAux.getJoinCallback(game, true));
	
	ui.createControl(
	    "Cancel Channel Button",
	    ControlStates.BUTTON(new Vector2f(.2f, -.15f), new Vector2f(.18f, .1f), "Cancel")
		    .setTextAlign(BitmapFont.Align.Center)
		    .setFontSize(20)
	).setCallback(CallbackType.CLICK, DISMISS_PROMPT);
	
	ui.concludeSection();
	
    }
    
}
