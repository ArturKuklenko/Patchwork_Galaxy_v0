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
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.network.oldmessages.ProtocolCallbacks;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;

public class ChatChannelAux {
    
    private ChatChannelAux() {}
    
    private static final UX2D ui = UX2D.getInstance();
    
    private static int _channelCount;
    private static boolean _listActive, _listIsGames;
    
    static final UICallback SHOW_CHANNEL_LIST = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    _listIsGames = false;
	    _listActive = true;
	    resetList();
	    createListUpdater();
	}
    };
    
    static final UICallback SHOW_GAMES_LIST = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    _listIsGames = true;
	    _listActive = true;
	    resetList();
	    createListUpdater();
	}
    };
    
    static final UICallback DISMISS_LIST = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    _listActive = false;
	    ui.killControls(
	        "Channel List Underlay", "Channel List Box",
		"Channel List Title", "Dismiss Channel List",
		"List Updater"
	    );
	    resetList();
	}
    };
    
    private static final ProtocolCallbacks HANDLE_LIST_UPDATE = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    try {
		if(!_listActive)
		    return;
		resetList();
		showList(
			result.getHumanMessage().split("\n"),
			_listIsGames ? "Listing public games" : "Listing public channels"
		);
	    }
	    catch(Exception e) {}
	}
	@Override
	public void fail(ProtocolMessage reason) {}
    };
    
    public static final ProtocolCallbacks JOINED_CHANNEL = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    ChatChannel.chatChannel();
	}
	@Override
	public void fail(ProtocolMessage reason) {
	    ui.createControl(
		    "Channel Error",
		    new ControlState()
			.setDimensions(new Vector2f(1f, .05f))
			.setText(new ColoredText().addText(reason.getHumanMessage(), ColorRGBA.Red))
			.setZIndex(500)
	    ).changeStateWithDuration(new ControlState().invalidate(), 5f);
	}
    };
    private static final ProtocolCallbacks JOINED_GAME = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    GameLobby.gameLobby();
	}
	@Override
	public void fail(ProtocolMessage reason) {
	    ui.createControl(
		    "Channel Error",
		    new ControlState()
			.setDimensions(new Vector2f(1f, .05f))
			.setText(new ColoredText().addText(reason.getHumanMessage(), ColorRGBA.Red))
			.setZIndex(500)
	    ).changeStateWithDuration(new ControlState().invalidate(), 5f);
	    _listActive = true;
	}
    };
    
    static void join(String name, String password, boolean game, boolean create) {
	String opcode = (create ? "JNEW" : "JOIN");
	String arg0 = game ? "game:" + name : name;
	String command = opcode + arg0 + ";;" + password;
	ProtocolMessage m = new ProtocolMessage(command);
	ProtocolCallbacks c = game ? JOINED_GAME : JOINED_CHANNEL;
	ClientManager.client().send(m, c);
    }
    
    static UICallback getJoinCallback(final String name, final String password, final boolean game, final boolean create) {
	return new UICallback() {
	    @Override
	    public void callback(UX2DControl control) {
		_listActive = false;
		join(
		    name == null ? ui.readControl("Channel Name") : name,
		    password == null ? ui.readControl("Channel Password") : password,
		    game,
		    create
		);
	    }
	};
    }
    
    static UICallback getJoinCallback(final boolean game, final boolean create) {
	return getJoinCallback(null, null, game, create);
    }
    
    private static void createListUpdater() {
	    ui.createControl(
		    "List Updater",
		    new ControlState()
	    ).setCallback(
		CallbackType.TICK,
		new UICallback() {
		    float cooldown = -1;
		    @Override
		    public void callback(UX2DControl control) {
			if(!_listActive) {
			    ui.killControl("List Updater");
			    return;
			}
		        if(cooldown > 0)
			    cooldown -= control.getTPF();
			else {
			    cooldown = 1;
			    ClientManager.client().send(
			    	new ProtocolMessage("LIST" + (_listIsGames ? "games" : "channels")),
			    	HANDLE_LIST_UPDATE
			    );
			}
		    }
		}
	    );
    }
    
    private static void resetList() {
	while(--_channelCount >= 0) {
	    ui.killControl("Channel #" + _channelCount);
	}
    }
    
    private static void showList(String[] channelDescriptors, String title) {
	
	if(!_listActive)
	    return;
	
	ui.createControl(
		"Channel List Underlay",
		ControlStates.INTERVENE()
		    .setZIndex(380)
	);
	
	ui.createControl(
		"Channel List Box",
		ControlStates.POPUP()
		    .setZIndex(390)
	);
	ui.createControl(
		"Channel List Title",
		new ControlState()
		    .setDimensions(new Vector2f(.6f, .03f))
		    .setCenter(new Vector2f(0f, .33f))
		    .setText(new ColoredText().addText(title, ColorRGBA.Yellow))
		    .setZIndex(400)
		    .setFontSize(20f)
		    .setTextAlign(BitmapFont.Align.Center)
	);
	
	ColoredText dismissOrange = new ColoredText().addText("(Dismiss)", ColorRGBA.Orange);
	ColoredText dismissRed = new ColoredText().addText("(Dismiss)", ColorRGBA.Red);
	ui.createControl(
		"Dismiss Channel List",
		new ControlState()
		    .setDimensions(new Vector2f(.1f, .05f))
		    .setCenter(new Vector2f(.3f, .3f))
		    .setText(dismissOrange)
		    .setZIndex(450)
	).setCallback(
		CallbackType.MOUSE_IN,
		Callbacks.stateChanger(
		    "Dismiss Channel List",
		    new ControlState().setText(dismissRed)
		)
	).setCallback(
		CallbackType.MOUSE_OUT,
		Callbacks.stateChanger(
		    "Dismiss Channel List",
		    new ControlState().setText(dismissOrange)
		)
	).setCallback(
		CallbackType.CLICK,
		DISMISS_LIST
	);
	
	populateList(channelDescriptors);
	
    }
    
    private static void populateList(String[] channelDescriptors) {
	resetList();
	_channelCount = channelDescriptors.length;
	for(int i = 0; i < channelDescriptors.length; ++i) {
	    String[] descriptor = channelDescriptors[i].split(";;");
	    ui.createControl(
		    "Channel #" + i,
		    new ControlState()
			.setDimensions(new Vector2f(.5f, .03f))
			.setCenter(new Vector2f(0f, .25f - (.035f * i)))
			.setText(parseChannelDescriptor(descriptor, false))
			.setZIndex(400)
			.setTextAlign(BitmapFont.Align.Center)
	    ).setCallback(
		    CallbackType.MOUSE_IN,
		    Callbacks.stateChanger("Channel #" + i, new ControlState()
			.setText(parseChannelDescriptor(descriptor, true))
			.setTextAlign(BitmapFont.Align.Center)
		    )
	    ).setCallback(
		    CallbackType.MOUSE_OUT,
		    Callbacks.stateChanger("Channel #" + i, null)
	    ).setCallback(
		    CallbackType.CLICK,
		    getJoinCallback(descriptor[0], "", _listIsGames, false)
	    );
	}
	ui.checkMousePos();
    }
    
    private static ColoredText parseChannelDescriptor(String[] descriptor, boolean active) {
	ColoredText result = new ColoredText();
	String name = descriptor[0];
	String suffix;
	if(_listIsGames)
	    suffix = "Host: " + descriptor[4];
	else
	    suffix = "Users: " + descriptor[3];
	if(!active)
	    result.addText(name + " (" + suffix + ")", ColorRGBA.LightGray);
	else {
	    result.addText(name, ColorRGBA.Yellow);
	    result.addText(" (" + suffix + ")", ColorRGBA.Orange);
	}
	return result;
    }
    
}
