package com.patchworkgalaxy.display.oldui.uidefs;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Effect;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.display.oldui.CallbackType;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.ControlState;
import com.patchworkgalaxy.display.oldui.TextInput;
import com.patchworkgalaxy.display.oldui.UICallback;
import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.oldui.UX2DControl;
import com.patchworkgalaxy.network.oldmessages.ProtocolCallbacks;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;

public class LoginPrompt {
    
    private LoginPrompt() { }
    
    private static final UX2D ui = UX2D.getInstance();
    
    private static boolean _register = false;
    public static String LOCAL_USERNAME;
    private static final ProtocolCallbacks _login2 = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    ChatChannel.chatChannel();
	}
	@Override
	public void fail(ProtocolMessage reason) {
	    Notifier.smallNotify(new ColoredText().addText(reason.getHumanMessage(), ColorRGBA.Red));
	}
    };
    private static final UICallback _login1 = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    String username = ui.readControl("Username").trim();
	    if(username.length() == 0) {
		ui.focusControl("Username");
		return;
	    }
	    if(_register) {
		String password = ui.readControl("Password");
		if(!password.equals(ui.readControl("Confirm Password"))) {
		    Notifier.smallNotify(new ColoredText().addText("Those passwords don't match", ColorRGBA.Red));
		    return;
		}
		login(
		    ui.readControl("Hostname"),
		    ui.readControl("Port"),
		    new ProtocolMessage("AREG" + username + ";;" + password + ";;" + ui.readControl("Email")),
		    new ProtocolCallbacks() {
		        @Override
		        public void succeed(ProtocolMessage reason) {
			    authPrompt();
			}
			@Override
			public void fail(ProtocolMessage reason) {
			    Notifier.smallNotify(new ColoredText().addText(reason.getHumanMessage(), ColorRGBA.Red));
			}
		    }
		);
	    }
	    else {
		login(
		    ui.readControl("Hostname"),
		    ui.readControl("Port"),
		    new ProtocolMessage("LGIN" + username + ";;" + ui.readControl("Password")),
		    _login2
		);
	    }
	    LOCAL_USERNAME = username;
	}
    };
    
    static String getLocalUsername() {
	return LOCAL_USERNAME;
    }
    
    private static final ProtocolCallbacks _auth2 = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    ClientManager.client().send(
		new ProtocolMessage("JOINMain Lobby"),
		ChatChannelAux.JOINED_CHANNEL
	    );
	}
	@Override
	public void fail(ProtocolMessage reason) {
	    Notifier.smallNotify(new ColoredText().addText(reason.getHumanMessage(), ColorRGBA.Red));
	}
    };
    
    private static final UICallback _auth1 = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    login(
		ui.readControl("Hostname"),
		ui.readControl("Port"),
		new ProtocolMessage("AFIN" + ui.readControl("Auth Token")),
		_auth2
	    );
	}
    };
    
    private static final ProtocolCallbacks _reset2 = new ProtocolCallbacks() {
	
	@Override
	public void succeed(ProtocolMessage result) {
	    ui.killSection("Reset Prompt");
	    ui.getControl("Password").clearInput();
	    ui.getControl("Password").focus();
	    Notifier.notify(
		new ColoredText().addText(
		    result.getHumanMessage()
		).addText(
		    " If you experience any difficulties, please contact "
		).addText(
		    "patrick@hailstormstudios.net",
		    ColorRGBA.Yellow
		).addText(".")
	    );
	}
	
	@Override
	public void fail(ProtocolMessage reason) {
	    Notifier.errorNotify(reason.getHumanMessage());
	}
	
    };
    
    private static final UICallback _reset1 = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    String username = ui.readControl("Reset Username");
	    String email = ui.readControl("Reset Address");
	    if(username.length() == 0)
		ui.focusControl("Reset Username");
	    else if(email.length() == 0)
		ui.focusControl("Reset Address");
	    else login(
		ui.readControl("Hostname"),
		ui.readControl("Port"),
		new ProtocolMessage("ARES" + username + ";;" + email),
		_reset2
	    );
	}
    };
    
    private static final UICallback _showReset = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    resetPrompt();
	}
    };
    
    public static void loginPrompt() {
	
	_register = false;
	
	ui.beginSection("Login Prompt");
	
	UICallback dismiss = new UICallback() {
		    @Override
		    public void callback(UX2DControl control) {
			ui.killSection("Login Prompt");
		    }
		};
	
	ui.createControl("Login Underlay", null, ControlStates.INTERVENE())
		.setCallback(CallbackType.CLICK, dismiss);
	ui.createControl("Login Box", null, ControlStates.POPUP());
	
	ui.createControl(
		"Login Button",
		ControlStates.BUTTON(new Vector2f(-.2f, -.1f), new Vector2f(.18f, .1f), "Login")
		    .setTextAlign(BitmapFont.Align.Center)
		    .setFontSize(20)
	).setCallback(
	    CallbackType.CLICK,
	    _login1
	).setCallback(
	    CallbackType.TICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    if(_register)
			control.setText("Register");
		    else
		        control.setText("Login");
		}
	    }
	);
	
	ui.createControl(
		"Cancel Button",
		null,
		ControlStates.BUTTON(new Vector2f(.2f, -.1f), new Vector2f(.18f, .1f), "Cancel")
		    .setTextAlign(BitmapFont.Align.Center)
		    .setFontSize(20)
	).setCallback(CallbackType.CLICK, dismiss);
	
	ui.createControl(
		"Username",
		new TextInput.Factory("Username: ", "> ", false, false),
		ControlStates.TEXTBOX(new Vector2f(0f, .3f), .4f)
	).setCallback(
	    CallbackType.SUBMIT,
	    _login1
	).focus();
	
	ui.createControl(
		"Password",
		new TextInput.Factory("Password: ", "> ", false, true),
		ControlStates.TEXTBOX(new Vector2f(-.1f, .225f), .3f)
	).setCallback(
	    CallbackType.SUBMIT,
	    _login1
	).pipeTextToInput("");
	
	ui.createControl(
	    "Show Reset Prompt",
	    ControlStates.TEXTBOX(new Vector2f(.31f, .225f), .09f)
		.setText("Reset your password")
		.setTextAlign(BitmapFont.Align.Center)
	).setCallback(
	    CallbackType.CLICK,
	    _showReset
	);
	
	ui.createControl(
		"Confirm Password",
		new TextInput.Factory("Confirm password: ", "> ", false, true),
		ControlStates.TEXTBOX(new Vector2f(0f, .225f), .4f)
		    .setZIndex(290)
		    .setOpacity(0)
	).setCallback(
	    CallbackType.SUBMIT,
	    _login1
	).pipeTextToInput("");
	
	ui.createControl(
		"Server Config",
		null,
		ControlStates.TEXTBOX(new Vector2f(-.2f, .1f), .15f).setText("Show Server Options").setTextAlign(BitmapFont.Align.Center)
	).setCallback(
		CallbackType.CLICK,
		Callbacks.toggle(
		    new ControlState().setText("Hide Server Options").setTextAlign(BitmapFont.Align.Center),
		    .01f,
		    Callbacks.combination(
			Callbacks.stateChangerWithDuration("Hostname", ControlStates.TEXTBOX(new Vector2f(0f, 0f), .4f), .3f),
			Callbacks.stateChangerWithDuration("Port", ControlStates.TEXTBOX(new Vector2f(0f, -.1f), .4f), .3f),
			Callbacks.stateChangerWithDuration(
			    "Login Button",
			    new ControlState(ui.getControl("Login Button").getDefaultState())
				.setCenter(new Vector2f(-.2f, -.3f)),
			    .3f
			),
			Callbacks.stateChangerWithDuration(
			    "Cancel Button",
			    new ControlState(ui.getControl("Cancel Button").getDefaultState())
				.setCenter(new Vector2f(.2f, -.3f)),
			    .3f
			)
		    ),
		    Callbacks.combination(
			Callbacks.stateChangerWithDuration("Hostname", null, .3f),
			Callbacks.stateChangerWithDuration("Port", null, .3f),
			Callbacks.stateChangerWithDuration("Login Button", null, .3f),
			Callbacks.stateChangerWithDuration("Cancel Button", null, .3f)
		    )
		)
	);
	
	ui.createControl(
		"Hostname",
		new TextInput.Factory("Server: ", "> ", false, false),
		ControlStates.TEXTBOX(new Vector2f(0f, .1f), .4f).setOpacity(0f)
	).setCallback(
	    CallbackType.SUBMIT,
	    _login1
	).pipeTextToInput("patchworkgalaxy.dyndns.org");
	
	ui.createControl(
		"Port",
		new TextInput.Factory("Port: ", "> ", false, false),
		ControlStates.TEXTBOX(new Vector2f(0f, .1f), .4f).setOpacity(0f)
	).setCallback(
	    CallbackType.SUBMIT,
	    _login1
	).pipeTextToInput("41342");
	
	ui.createControl(
		"Email",
		new TextInput.Factory("Email: ", "> ", false, false),
		ControlStates.TEXTBOX(new Vector2f(.2f, .1f), .15f).setOpacity(0f)
	).setCallback(
	    CallbackType.SUBMIT,
	    _login1
	);
	
	ui.createControl(
		"Register",
		null,
		ControlStates.TEXTBOX(new Vector2f(.2f, .1f), .15f)
		    .setText("Register New Account")
		    .setTextAlign(BitmapFont.Align.Center)
	).setCallback(
		CallbackType.CLICK,
		Callbacks.toggle(
		    Callbacks.combination(
			Callbacks.stateChangerWithDuration("Register",
			    ControlStates.TEXTBOX(new Vector2f(.2f, .14f), .15f)
				.setText("Cancel Registration")
				.setTextAlign(BitmapFont.Align.Center),
			    .15f),
			Callbacks.stateChangerWithDuration(
			    "Password", 
			    ControlStates.TEXTBOX(new Vector2f(0f, .25f), .4f),
			    .15f),
			Callbacks.stateChangerWithDuration(
			    "Confirm Password", 
			    ControlStates.TEXTBOX(new Vector2f(0f, .20f), .4f).setOpacity(1f),
			    .15f),
			Callbacks.stateChangerWithDuration("Email", ControlStates.TEXTBOX(new Vector2f(.2f, .07f), .2f), .15f),
			new UICallback() {
			    @Override
			    public void callback(UX2DControl control) {
				_register = true;
				ui.focusControl("Email");
			    }
			}
		    ),
		    Callbacks.combination(
			Callbacks.stateChangerWithDuration("Register", null, .15f),
			Callbacks.stateChangerWithDuration("Email", null, .15f),
			Callbacks.stateChangerWithDuration("Password", null, .15f),
			Callbacks.stateChangerWithDuration("Confirm Password", null, .15f),
			new UICallback() {
			    @Override
			    public void callback(UX2DControl control) {
				_register = false;
				ui.focusControl("Username");
			    }
			}
		    )
		)
	);
	
	ui.concludeSection();
	
    }
    
    public static void authPrompt() {
	ui.beginSection("Auth Prompt");
	
	ui.createControl(
	    "Auth Underlay",
	    ControlStates.INTERVENE()
		.setZIndex(490)
	);
	
	ui.createControl(
	    "Auth Box",
	    ControlStates.POPUP()
		.setZIndex(500)
		.setTextAlign(BitmapFont.Align.Center)
		.setText(
		    new ColoredText()
		    .addText("\n\nRegistration successful.", ColorRGBA.Yellow)
		    .addText("\n\nAn email has been send to the address you specified containing a security token."
			+ "\nPlease input that token to procede.\n\n\nIf you did not receive an email or entered an incorrect address,"
			+ "\npress cancel and reregister, using the same username.")
		)
		.setOffset(new Vector2f(0f, -.01f))
		.setFontSize(16)
	);
	
	ui.createControl(
	    "Auth Token",
	    new TextInput.Factory("Security token: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, -.18f), .4f)
		.setZIndex(510)
	).setCallback(
	    CallbackType.SUBMIT,
	    _auth1
	).focus();
	
	ui.createControl(
		"Auth Button",
		ControlStates.BUTTON(new Vector2f(-.2f, -.3f), new Vector2f(.15f, .1f) , "Submit")
		    .setZIndex(510)
		    .setFontSize(16)
	).setCallback(
	    CallbackType.CLICK,
	    _auth1
	);
	
	ui.createControl(
		"Cancel Auth Button",
		ControlStates.BUTTON(new Vector2f(.2f, -.3f), new Vector2f(.15f, .1f), "Cancel")
		    .setZIndex(510)
		    .setFontSize(16)
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    ui.killSection("Auth Prompt");
		}
	    }
	);
	
	ui.concludeSection();
    }    
    
    public static void resetPrompt() {
	ui.beginSection("Reset Prompt");
	
	ui.createControl(
	    "Reset Underlay",
	    ControlStates.INTERVENE()
		.setZIndex(490)
	);
	
	ui.createControl(
	    "Reset Box",
	    ControlStates.POPUP()
		.setZIndex(500)
		.setTextAlign(BitmapFont.Align.Center)
		.setText(
		    new ColoredText()
			.addText("\n\nReseting your password", ColorRGBA.Yellow)
			.addText("\n\nPlease enter your username here. For security, also enter your email address.")
		)
		.setOffset(new Vector2f(0f, -.01f))
		.setFontSize(16)
	);
	
	ui.createControl(
	    "Reset Username",
	    new TextInput.Factory("Username: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, 0f), .4f)
		.setZIndex(510)
	).setCallback(
	    CallbackType.SUBMIT,
	    _reset1
	).focus();
	
	ui.createControl(
	    "Reset Address",
	    new TextInput.Factory("Email address: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, -.1f), .4f)
		.setZIndex(510)
	).setCallback(
	    CallbackType.SUBMIT,
	    _reset1
	);
	
	ui.createControl(
		"Reset Button",
		ControlStates.BUTTON(new Vector2f(-.2f, -.3f), new Vector2f(.15f, .1f) , "Submit")
		    .setZIndex(510)
		    .setFontSize(16)
	).setCallback(
	    CallbackType.CLICK,
	    _reset1
	);
	
	ui.createControl(
		"Cancel Reset Button",
		ControlStates.BUTTON(new Vector2f(.2f, -.3f), new Vector2f(.15f, .1f), "Cancel")
		    .setZIndex(510)
		    .setFontSize(16)
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    ui.killSection("Reset Prompt");
		}
	    }
	);
	
	ui.concludeSection();
    }
    
    private static void login(String hostname, String port, final ProtocolMessage m, final ProtocolCallbacks c) {
	ClientManager.connect(hostname, port)
		.then(new Runnable() {
		    @Override public void run() {
			ClientManager.client().send(m, c);
		    }
		})
		.onError(new Effect<Throwable>() {
		    @Override public void execute(Throwable input) {
			Notifier.errorNotify(input.getLocalizedMessage());
		    }
		});
    }
    
}
