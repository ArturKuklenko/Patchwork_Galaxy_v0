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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GameLobby {
    
    private GameLobby() { }
    
    private static UX2D ui = UX2D.getInstance();
    
    private static final UICallback LEAVE = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    leave();
	}
    };
    
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
    
    static UserData getLocalUser() {
	return ClientManager.client().getChannelData().getUserData(LoginPrompt.getLocalUsername());
    }
    
    static UserData getHost() {
	String host = ClientManager.client().getChannelData().getHostUsername();
	return ClientManager.client().getChannelData().getUserData(host);
    }
    
    private static final UICallback TOGGLE_OBSERVER_STATUS = new UICallback() {
	@Override public void callback(UX2DControl control) {
	    boolean observing = getLocalUser().booleanDatum(SpecialKeys.OBSERVER);
	    ProtocolMessage pm = new ProtocolMessage("UDAT" + SpecialKeys.OBSERVER + ";;" + !observing);
	    ClientManager.client().send(pm,
		    new ProtocolCallbacks() {
			@Override public void succeed(ProtocolMessage result) {}
			@Override public void fail(ProtocolMessage reason) {}	
		    });
	}
    };
    
    private static final UICallback CHANNEL_MONITOR = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    if(_hostLeft)
		return;
	    if(_name == null) {
		_name = ClientManager.client().getChannelData().getChannelName();
		ui.setControlText(
		    "Game Title",
		    new ColoredText("\n\tLobby: ").addText(_name, ColorRGBA.Yellow)
		);
	    }
	    ChannelData channel = ClientManager.client().getChannelData();
	    ArrayList<UserData> userData = new ArrayList<>(channel.getUserDatas());
	    String host = null;
	    String opponent = null;
	    List<UserData> observers = new ArrayList<>();
	    for(UserData udat : userData) {
		if(udat.booleanDatum(SpecialKeys.OBSERVER)) {
		    observers.add(udat);
		    continue;
		}
		if(udat.booleanDatum(SpecialKeys.HOST))
		    host = udat.getUsername();
		else
		    opponent = udat.getUsername();
	    }
	    setOpponent(opponent);
	    if(_initialized && host == null && !_amHost)
		hostLeft();
	    else {
		if(getLocalUser().booleanDatum(SpecialKeys.HOST))
		    amHost();
		setHost(host);
	    }
	    _initialized = true;
	    showFaction(true);
	    showFaction(false);
	    if(getHost() == null) return;
	    if(getHost().booleanDatum(SpecialKeys.ALLOWS_SPECTATORS))
		setObservers(observers);
	}
    };
    
    private static void showFaction(boolean host) {
	String where = host ? "Host Faction" : "Opponent Faction";
	String name = host ? _host : _opponent;
	if(name == null) {
	    ui.setControlText(where, "Faction: ");
	}
	else {
	    String faction = ClientManager.client().getChannelData().getDatum(name, SpecialKeys.FACTION);
	    if(faction == null)
		faction = "Ketriaava";
	    ColorRGBA color = colormap.get(faction);
	    ui.setControlText(where, new ColoredText("Faction: ").addText(faction, color));
	}
    }
    
    private static final ProtocolCallbacks START_GAME = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {}

	@Override
	public void fail(ProtocolMessage reason) {
	    Notifier.notify("Unable to start game: " + reason.getHumanMessage());
	}	
    };
    private static UICallback START_BUTTON_CLICKED = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    if(_amHost) {
		ClientManager.client().send(
		    new ProtocolMessage("GAME"),
		    START_GAME
		);
	    }
	    else {
		ClientManager.client().send(
		    new ProtocolMessage("UDAT" + SpecialKeys.READY + ";;true"),
		    null
		);
	    }
	}
    };
    
    static final ColorRGBA[] factionColors = { ColorRGBA.Green, ColorRGBA.Orange, ColorRGBA.Blue, ColorRGBA.LightGray};
//    static final String[] factions = { "Ketriaava", "Horizon", "Svagh-Ti", "Gliseans" };
    static final String[] factions = { "Ketriaava" };

    static final Map<String, ColorRGBA> colormap = new HashMap<>();
    static {
	for(int i = factions.length; --i >= 0;)
	    colormap.put(factions[i], factionColors[i]);
    }
    private static ProtocolCallbacks SET_FACTION = new ProtocolCallbacks() {
	@Override public void succeed(ProtocolMessage result) {}
	@Override public void fail(ProtocolMessage reason) {}
    };
    
    private static String _name, _host, _opponent;
    private static boolean _amHost, _initialized, _hostLeft;
    
    public static void gameLobby() {
	ui.killAllControls();
	_name = _host = _opponent = null;
	_amHost = false;
	
	ui.createControl(
	    "Channel Monitor",
	    new ControlState()
	).setCallback(
	    CallbackType.TICK,
	    CHANNEL_MONITOR
	);
	
	ui.createControl(
	    "Lobby Chat",
	    new ControlState()
	        .setDimensions(new Vector2f(.6f, .6f))
	        .setCenter(new Vector2f(-.3f, 0f))
	        .setBackground("Interface/chat.png")		
	).attachChatLog(45);
	
	ui.createControl(
		"Lobby Chat Text",
		new TextInput.Factory("", "> ", false, false),
		ControlStates.TEXTBOX(new Vector2f(-.3f, -.65f), .6f)
	).setCallback(
		CallbackType.SUBMIT,
		SEND_CHAT_MESSAGE
	).focus();
	
	ui.createControl(
	    "Players Panel",
	    new ControlState()
		.setCenter(new Vector2f(.65f, .18f))
		.setDimensions(new Vector2f(.3f, .65f))
		.setBackground("Interface/panel.png")
		.setZIndex(200)
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    hideFactionList();
	        }			
	    }
	);
	
	ui.createControl(
	    "Host Panel",
	    new ControlState()
		.setCenter(new Vector2f(.65f, .53f))
		.setDimensions(new Vector2f(.23f, .2f))
		.setBackground("Interface/offblack.png")
		.setZIndex(205)
	);
	
	ui.createControl(
	    "Host Username",
	    ControlStates.TEXTBOX(new Vector2f(.65f, .62f), .2f)
	        .setZIndex(210)
		.setFontSize(14)
	        .setText("Player 1: ")
	);
	
	ui.createControl(
	    "Host Faction",
	    ControlStates.TEXTBOX(new Vector2f(.65f, .57f), .2f)
	        .setZIndex(210)
		.setFontSize(14)
	        .setText(new ColoredText("Faction: ").addText("Ketriaava", ColorRGBA.Green))
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    if(_amHost)
			showFactionList();
	        }			
	    }
	);
	
	ui.createControl(
	    "Opponent Panel",
	    new ControlState()
		.setCenter(new Vector2f(.65f, 0f))
		.setDimensions(new Vector2f(.23f, .2f))
		.setBackground("Interface/offblack.png")
		.setZIndex(205)
	);
	
	ui.createControl(
	    "Opponent Username",
	    ControlStates.TEXTBOX(new Vector2f(.65f, .09f), .2f)
	        .setZIndex(210)
	        .setFontSize(14)
	        .setText("Player 2: ")
	);
	
	ui.createControl(
	    "Opponent Faction",
	    ControlStates.TEXTBOX(new Vector2f(.65f, .04f), .2f)
	        .setZIndex(210)
		.setFontSize(14)
	        .setText(new ColoredText("Faction: ").addText("Ketriaava", ColorRGBA.Green))
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    if(!_amHost)
			showFactionList();
	        }			
	    }
	);
	
	ui.createControl(
	    "Game Title",
	    new ControlState()
		.setCenter(new Vector2f(-.3f, .8f))
		.setDimensions(new Vector2f(.6f, .1f))
		.setFontSize(20)
		.setZIndex(200)
		.setBackground("Interface/panel.png")
	);
	
	ui.createControl(
	    "Start Game",
	    new ControlState()
		.setCenter(new Vector2f(.5f, -.6f))
		.setDimensions(new Vector2f(.125f, .07f))
		.setZIndex(300)
		.setBackground("Interface/button.png")
		.setText("Ready")
		.setOffset(new Vector2f(0f, -.01f))
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(20)
	).setCallback(
	    CallbackType.CLICK,
	    START_BUTTON_CLICKED
	);
	
	ui.createControl(
	    "Leave Game",
	    new ControlState()
	        .setCenter(new Vector2f(.8f, -.6f))
		.setDimensions(new Vector2f(.125f, .07f))
		.setZIndex(300)
		.setBackground("Interface/button.png")
		.setText("Cancel")
		.setOffset(new Vector2f(0f, -.01f))
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(20)
	).setCallback(
		CallbackType.CLICK,
		LEAVE
	);
		
	
    }
    
    private static void amHost() {
	if(ui.getControl("Start Game") != null)
	    ui.setControlText("Start Game", "Start");
	_amHost = true;
    }
    
    private static void setHost(String name) {
	if(name == null)
	    name = "";
	_host = name;
	ColorRGBA namecolor = _amHost ? ColorRGBA.Green : ColorRGBA.Red;
	ui.setControlText(
	    "Host Username",
	    new ColoredText("Player 1: ").addText(name, namecolor)
	);
    }
    
    private static void setOpponent(String name) {
	if(name == null)
	    name = "";
	_opponent = name;
	ColorRGBA namecolor = _amHost ? ColorRGBA.Red : ColorRGBA.Green;
	ui.setControlText(
	    "Opponent Username",
	    new ColoredText("Player 2: ").addText(name, namecolor)
	);
    }
    
    private static void setObservers(List<UserData> observers) {
	if(ui.getControl("Lobby Chat") == null) {
	    ui.killSection("Observers Panel");
	    return;
	}
	Iterator<UserData> i = observers.iterator();
	StringBuilder text = new StringBuilder("Observers: ");
	text.append(i.hasNext() ? i.next().getUsername() : "None");
	while(i.hasNext())
	    text.append(", ").append(i.next().getUsername());
	if(ui.getControl("Observers") == null)
	    initObservers();
	else {
	    boolean observing = getLocalUser().booleanDatum(SpecialKeys.OBSERVER);
	    ui.setControlText("Observers", text.toString());
	    if(!getLocalUser().booleanDatum(SpecialKeys.HOST))
		ui.setControlText("Observe Button", observing ? "Play" : "Observe");
	}
	if(ui.getControl("Lobby Chat") == null)
	    ui.killSection("Observers Panel");
    }
    
    private static void initObservers() {
	ui.beginSection("Observers Panel");
	ui.createControl("Observers",
		ControlStates.TEXTBOX(new Vector2f(.65f, -.3f), .25f)
		    .setZIndex(220)
		    .setText("Observers: None")
		    );
	if(!getLocalUser().booleanDatum(SpecialKeys.HOST)) {
	    ui.createControl("Observe Button",
		    ControlStates.BUTTON(new Vector2f(.65f, -.4f), new Vector2f(.15f, .05f), "Observe")
		    ).setCallback(CallbackType.CLICK, TOGGLE_OBSERVER_STATUS);
	}
	ui.concludeSection();
    }
    
    private static void hostLeft() {
	_hostLeft = true;
	ui.createControl(
	    "Host Left",
	    ControlStates.POPUP()
		.setText(new ColoredText().addText("The game host has left", ColorRGBA.Red))
		.setFontSize(16)
		.setOffset(new Vector2f(0f, -.1f))
		.setTextAlign(BitmapFont.Align.Center)
	).setCallback(
	    CallbackType.CLICK,
	    LEAVE
	);
	
	ui.createControl(
	    "Host Left OK",
	    ControlStates.BUTTON(new Vector2f(0f, 0f), new Vector2f(.2f, .1f), "Okay")
		.setFontSize(20)
		.setTextAlign(BitmapFont.Align.Center)
	).setCallback(
	    CallbackType.CLICK,
	    LEAVE
	);
    }
    
    private static void showFactionList() {
	float y = _amHost ? .57f : .04f;
	String where = _amHost ? "Host Faction" : "Opponent Faction";
	
	ui.setControlText(where, new ColoredText().addText("Select faction", ColorRGBA.Yellow));
	
	ui.beginSection("Faction List");
	
	ui.createControl(
	    "Faction List Underlay",
	    ControlStates.PANEL(new Vector2f(.6f, y - .09f), new Vector2f(.15f, .15f))
		.setZIndex(205)
	);
	
	for(int i = 0; i < factions.length; ++i) {
	    y -= .054f;
	    final String faction = factions[i];
	    ColorRGBA color = factionColors[i];
	    ui.createControl(
		faction,
		new ControlState()
		    .setCenter(new Vector2f(.65f, y))
		    .setDimensions(new Vector2f(.2f, .025f))
		    .setZIndex(210)
		    .setOpacity(.5f)
		    .setFontSize(14)
		    .setBackground("Interface/offblack.png")
		    .setText(
			new ColoredText("         ").addText(faction, color)
		    )
	    ).setCallback(
		CallbackType.CLICK,
		new UICallback() {
		    @Override
		    public void callback(UX2DControl control) {
			ClientManager.client().send(
				new ProtocolMessage("UDAT" + SpecialKeys.FACTION + ";;" + faction),
				SET_FACTION
				);
			hideFactionList();
		    }			
		}
	    ).setCallback(
		CallbackType.MOUSE_IN,
		Callbacks.stateChangerWithDuration(
		    faction,
		new ControlState(ui.getControl(faction).getDefaultState())
		    .setOpacity(1f)
		    .setBackground("Interface/offblack.png"),
		.3f
		)
	    ).setCallback(
		CallbackType.MOUSE_OUT,
		Callbacks.stateChangerWithDuration(
		    faction,
		    null,
		    .3f
		)
	    );
	}	
	
	ui.concludeSection();
    }
    
    private static void hideFactionList() {
	ui.killSection("Faction List");
    }
    
    public static void leave() {
	ClientManager.client().send(
	    new ProtocolMessage("JBAK"),
	    ChatChannelAux.JOINED_CHANNEL
	);
    }
    
}
