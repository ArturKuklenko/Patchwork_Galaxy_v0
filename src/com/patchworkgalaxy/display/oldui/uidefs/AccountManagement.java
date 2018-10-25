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

public class AccountManagement {
    
    private static UX2D ui = UX2D.getInstance();
    
    private static UICallback DISMISS = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    hide();
	}
    };
    
    private static UICallback ACCOUNT_INFO = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    accountInfo();
	}
    };
    
    private static UICallback CHANGE_PASSWORD = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    changePassword();
	}
    };
    
    private static UICallback CHANGE_EMAIL = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    changeEmail();
	}
    };
    
    private static ProtocolCallbacks POST_SUBMIT = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    authPrompt();
	}
	@Override
	public void fail(ProtocolMessage reason) {
	    Notifier.errorNotify(reason.getHumanMessage());
	}
    };
    
    private static ProtocolCallbacks POST_TOKEN = new ProtocolCallbacks() {
	@Override
	public void succeed(ProtocolMessage result) {
	    accountInfo();
	    Notifier.notify(
		new ColoredText().addText(
		    "Security token accepted. Account updated.", ColorRGBA.Green
		).addText(
		    "\n\nIf you experience any difficulties, contact "
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
    
    private static UICallback SUBMIT_TOKEN = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    String token = ui.readControl("Auth Token");
	    if(token.length() != 6)
		Notifier.smallNotify(new ColoredText().addText("Invalid security token (should be six characters)", ColorRGBA.Red));
	    else
		ClientManager.client().send(new ProtocolMessage("UMANFinish;;" + token), POST_TOKEN);
	}
    };
    
    private static UICallback SUBMIT_PASSWORD = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    String password = ui.readControl("Old Password");
	    String newPassword = ui.readControl("New Password");
	    String confPassword = ui.readControl("Confirm Password");
	    if(password.length() < 6) {
		Notifier.smallNotify(new ColoredText().addText("Current password required", ColorRGBA.Red));
		ui.focusControl("Old Password");
	    }
	    else if(newPassword.length() < 6)
		Notifier.smallNotify(new ColoredText().addText("That password is too short (should be at least six characters)", ColorRGBA.Red));
	    else if(newPassword.equals(confPassword))
		ClientManager.client().send(new ProtocolMessage("UMANPassword;;" + password + ";;" + newPassword), POST_SUBMIT);
	    else
		Notifier.smallNotify(new ColoredText().addText("Those passwords don't match", ColorRGBA.Red));
		
	}
    };
    
    private static UICallback SUBMIT_EMAIL = new UICallback() {
	@Override
	public void callback(UX2DControl control) {
	    String password = ui.readControl("Old Password");
	    String newEmail = ui.readControl("New Email");
	    String confEmail = ui.readControl("Confirm Email");
	    if(password.length() < 6) {
		Notifier.smallNotify(new ColoredText().addText("Current password required", ColorRGBA.Red));
	    }
	    else if(newEmail.equals(confEmail))
		ClientManager.client().send(new ProtocolMessage("UMANEmail;;" + password + ";;" + newEmail), POST_SUBMIT);
	    else
		Notifier.smallNotify(new ColoredText().addText("Those addresses don't match", ColorRGBA.Red));
		
	}
    };
    
    private AccountManagement() {}
    
    public static void show() {
	
	ui.beginSection("Account Management");
	
	ui.createControl(
	    "Management Underlay",
	    ControlStates.INTERVENE()
	);
	
	ui.createControl(
	    "Management Box",
	    ControlStates.POPUP()
	);
	
	ui.createControl(
	    "Tabs",
	    ControlStates.DARKPANEL(
		new Vector2f(0f, .4f),
		new Vector2f(.5f, .1f)
	    )
	);
	
	ui.createControl(
	    "Account Info",
	    ControlStates.BUTTON(
		new Vector2f(-.3f, .4f),
		new Vector2f(.18f, .08f),
		new ColoredText().addText("Account Info", ColorRGBA.Yellow)
	    ).setFontSize(
		16
	    )
	);
	
	ui.createControl(
	    "Contacts",
	    ControlStates.BUTTON(
		new Vector2f(.1f, .4f),
		new Vector2f(.18f, .08f),
		new ColoredText().addText("Contacts", ColorRGBA.Yellow)
	    ).setFontSize(
		16
	    )
	).setCallback(
	    CallbackType.CLICK,
	    new UICallback() {
		@Override
		public void callback(UX2DControl control) {
		    Notifier.smallNotify(new ColoredText().addText("This feature is currently unavailable", ColorRGBA.Yellow));
		}
	    }
	);
	
	ui.createControl(
	    "Dismiss",
	    ControlStates.BUTTON(
		new Vector2f(.395f, .4f),
		new Vector2f(.08f, .08f),
		new ColoredText().addText("Dismiss", ColorRGBA.Red)
	    ).setFontSize(
		16
	    )
	).setCallback(
	    CallbackType.CLICK,
	    DISMISS
	);
	
	ui.concludeSection();
	
	accountInfo();
	
    }
    
    static void accountInfo() {
	
	ui.killSection("Manage Contacts");
	ui.killSection("Change Password");
	ui.killSection("Change Email");
	ui.killSection("Auth Prompt");
	
	ui.beginSection("Account Info");
	
	ui.createControl(
	    "Account Info Underlay",
	    ControlStates.DARKPANEL(new Vector2f(0f, 0f), new Vector2f(.4f, .25f))
	);
	
	ui.createControl(
	    "Username",
	    ControlStates.HUGE_TEXTBOX(new Vector2f(0f, .12f), .3f)
		.setText(
		    new ColoredText("Profile: ").addText("[this is a dummy profile]", ColorRGBA.Yellow)
		).setBackground("")
	);
	
	ui.createControl(
	    "Games Played",
	    ControlStates.BIG_TEXTBOX(new Vector2f(0f, .03f), .3f)
		.setText("Games Played: 000")
		.setBackground("")
	);
	
	ui.createControl(
	    "Wins",
	    ControlStates.TEXTBOX(new Vector2f(0f, -.03f), .3f)
		.setText("Won: 000")
		.setBackground("")
	);
	
	ui.createControl(
	    "Losses",
	    ControlStates.TEXTBOX(new Vector2f(0f, -.08f), .3f)
		.setText("Lost: 000")
		.setBackground("")
	);
	
	ui.createControl(
	    "Draws",
	    ControlStates.TEXTBOX(new Vector2f(0f, -.13f), .3f)
		.setText("Drawn: 000")
		.setBackground("")
	);
	
	ui.createControl(
	    "Change Password",
	    ControlStates.BUTTON(new Vector2f(-.17f, -.37f), new Vector2f(.15f, .1f), "Update password")
	).setCallback(
	    CallbackType.CLICK,
	    CHANGE_PASSWORD
	);
	
	ui.createControl(
	    "Change Email",
	    ControlStates.BUTTON(new Vector2f(.17f, -.37f), new Vector2f(.15f, .1f), "Update Email Address")
	).setCallback(
	    CallbackType.CLICK,
	    CHANGE_EMAIL
	);
	
	ui.concludeSection();
	
    }
    
    static void changePassword() {
	
	ui.killSection("Change Email");
	ui.beginSection("Change Password");
	
	ui.createControl(
	    "Change Password Panel",
	    ControlStates.PANEL(new Vector2f(0f, 0f), new Vector2f(.4f, .25f))
		.setZIndex(400)
	);
	
	ui.createControl(
	    "Update Label",
	    new ControlState()
		.setCenter(new Vector2f(0f, .13f))
		.setDimensions(new Vector2f(.4f, .1f))
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(16)
		.setText(new ColoredText().addText("Updating account password", ColorRGBA.Yellow))
		.setZIndex(410)
	);
	
	ui.createControl(
	    "Old Password",
	    new TextInput.Factory("Old password: ", "> ", false, true),
	    ControlStates.TEXTBOX(new Vector2f(0f, .1f), .3f)
		.setZIndex(410)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_PASSWORD
	).focus();
	
	ui.createControl(
	    "New Password",
	    new TextInput.Factory("New password: ", "> ", false, true),
	    ControlStates.TEXTBOX(new Vector2f(0f, 0f), .3f)
		.setZIndex(410)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_PASSWORD
	);
	
	ui.createControl(
	    "Confirm Password",
	    new TextInput.Factory("Confirm password: ", "> ", false, true),
	    ControlStates.TEXTBOX(new Vector2f(0f, -.05f), .3f)
		.setZIndex(410)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_PASSWORD
	);
	
	ui.createControl(
	    "Submit Change",
	    ControlStates.BUTTON(new Vector2f(-.15f, -.15f), new Vector2f(.1f, .05f), "Update")
		.setZIndex(420)
	).setCallback(
	    CallbackType.CLICK,
	    SUBMIT_PASSWORD
	);
	
	ui.createControl(
	    "Cancel Change",
	    ControlStates.BUTTON(new Vector2f(.15f, -.15f), new Vector2f(.1f, .05f), "Cancel")
		.setZIndex(420)
	).setCallback(
	    CallbackType.CLICK,
	    ACCOUNT_INFO
	);
	
	ui.concludeSection();
	
    }
    
    static void changeEmail() {
	
	ui.killSection("Change Password");
	ui.beginSection("Change Email");
	
	ui.createControl(
	    "Change Email Panel",
	    ControlStates.PANEL(new Vector2f(0f, 0f), new Vector2f(.4f, .25f))
		.setZIndex(400)
	);
	
	ui.createControl(
	    "Update Label",
	    new ControlState()
		.setCenter(new Vector2f(0f, .13f))
		.setDimensions(new Vector2f(.4f, .1f))
		.setTextAlign(BitmapFont.Align.Center)
		.setFontSize(16)
		.setText(new ColoredText().addText("Updating email address", ColorRGBA.Yellow))
		.setZIndex(410)
	);
	
	ui.createControl(
	    "Old Password",
	    new TextInput.Factory("Password: ", "> ", false, true),
	    ControlStates.TEXTBOX(new Vector2f(0f, .1f), .3f)
		.setZIndex(410)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_EMAIL
	).focus();
	
	ui.createControl(
	    "New Email",
	    new TextInput.Factory("New address: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, 0f), .3f)
		.setZIndex(410)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_EMAIL
	);
	
	ui.createControl(
	    "Confirm Email",
	    new TextInput.Factory("Confirm address: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, -.05f), .3f)
		.setZIndex(410)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_EMAIL
	);
	
	ui.createControl(
	    "Submit Change",
	    ControlStates.BUTTON(new Vector2f(-.15f, -.15f), new Vector2f(.1f, .05f), "Update")
		.setZIndex(420)
	).setCallback(
	    CallbackType.CLICK,
	    SUBMIT_EMAIL
	);
	
	ui.createControl(
	    "Cancel Change",
	    ControlStates.BUTTON(new Vector2f(.15f, -.15f), new Vector2f(.1f, .05f), "Cancel")
		.setZIndex(420)
	).setCallback(
	    CallbackType.CLICK,
	    ACCOUNT_INFO
	);
	
	ui.concludeSection();
	
    }
    
    static void authPrompt() {
	    
	ui.beginSection("Auth Prompt");
	
	ui.createControl(
	    "Auth Underlay",
	    ControlStates.INTERVENE()
		.setZIndex(590)
	);
	
	ui.createControl(
	    "Auth Box",
	    ControlStates.POPUP()
		.setZIndex(600)
		.setTextAlign(BitmapFont.Align.Center)
		.setText(
		    new ColoredText()
		    .addText("\n\nAccount update pending.", ColorRGBA.Yellow)
		    .addText("\n\nAn email has been send to the address we have on file containing a security token."
			+ "\nPlease input that token to confirm the change. ")
		    .addText("Nothing will change until you do.", ColorRGBA.Yellow)
		    .addText(" This is a security measure.")
		    .addText("\n\n\nIf you did not receive an email or entered an incorrect address,"
			+ "\npress cancel and repeat the process, or contact ")
		    .addText("patrick@hailstormstudios.net", ColorRGBA.Yellow)
		    .addText(".")
		)
		.setOffset(new Vector2f(0f, -.01f))
		.setFontSize(16)
	);
	
	ui.createControl(
	    "Auth Token",
	    new TextInput.Factory("Security token: ", "> ", false, false),
	    ControlStates.TEXTBOX(new Vector2f(0f, -.18f), .4f)
		.setZIndex(610)
	).setCallback(
	    CallbackType.SUBMIT,
	    SUBMIT_TOKEN
	).focus();
	
	ui.createControl(
		"Auth Button",
		ControlStates.BUTTON(new Vector2f(-.2f, -.3f), new Vector2f(.15f, .1f) , "Submit")
		    .setZIndex(610)
		    .setFontSize(16)
	).setCallback(
	    CallbackType.CLICK,
	    SUBMIT_TOKEN
	);
	
	ui.createControl(
		"Cancel Auth Button",
		ControlStates.BUTTON(new Vector2f(.2f, -.3f), new Vector2f(.15f, .1f), "Cancel")
		    .setZIndex(610)
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
    
    public static void hide() {
	ui.killSection("Account Management");
	ui.killSection("Account Info");
	ui.killSection("Change Password");
	ui.killSection("Change Email");
	ui.killSection("Auth Prompt");
    }
    
}
