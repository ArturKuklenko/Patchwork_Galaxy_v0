package com.patchworkgalaxy.display.ui.defs.login;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.Effect;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.client.PWGClient;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.uidefs.ChatChannel;
import com.patchworkgalaxy.display.oldui.uidefs.LoginPrompt;
import com.patchworkgalaxy.display.oldui.uidefs.Notifier;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.WriteAction;
import com.patchworkgalaxy.general.subscriptions.Topic;
import com.patchworkgalaxy.network.transaction.TransactionType;
import java.util.Map;

class LoginButtonCD extends StandardComponentDescriptor {
    
    private static final Vector2f SIZE = new Vector2f(.1f, .1f);
    private static Vector2f CENTER = new Vector2f(-.3f, -.4f);
    
    LoginButtonCD(int index) {
	super("Login", CENTER, SIZE);
	this
		.setBackgroundImage("Interface/pwgui/buttons/button_bright.png")
		.setText("Submit")
		.setTooltip("Log in!")
		.setTextAlignCenter()
		.setTextVAlignCenter()
		.setTextSize(16f)
		.addCallback(new WriteAction(1f, Property.OPACITY).asCallback(ComponentCallback.Type.MOUSE_IN))
		.addCallback(new WriteAction(.8f, Property.OPACITY).asCallback(ComponentCallback.Type.MOUSE_OUT))
		.setZIndex(Definitions.Z_INDEX_MED + 10)
		.addTransition(.05f, Property.OPACITY)
		.setHotkey(Definitions.KEY_DUMMY)
		.addCallback(LOGIN_ACTION.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.addCallback(UPDATE_TOOLTIP_ACTION.asCallback(ComponentCallback.Type.UPDATE))
		.addCallback(UPDATE_TOOLTIP_ACTION.asCallback(ComponentCallback.Type.INITIALIZE))
		.addSubscription(Topic.UI_KEY_PRESSED)
		.addSubscription(Topic.UI_CONTROL_CLICKED)
		;
    }
    
    private static final Action UPDATE_TOOLTIP_ACTION = new Action() {
	@Override public void act(Component actOn) {
	    Panel loginPanel = actOn.getPanel();
	    Panel registerPanel = UI.Instance.getPanelWithTag("Register");
	    actOn.write(getLoginStatus(loginPanel, registerPanel), Property.TOOLTIP_TEXT);
	}
    };
    
    private static final Action LOGIN_ACTION = new Action() {
	@Override public void act(Component actOn) {
	    Panel loginPanel = actOn.getPanel();
	    Panel registerPanel = UI.Instance.getPanelWithTag("Register");
	    login(loginPanel, registerPanel);
	}
    };
    
    private static ColoredText getLoginStatus(Panel login, Panel register) {
	LoginComponentGatherer gatherer = new LoginComponentGatherer(false, login, register);
	gatherer.gatherStrings(login, register);
	String error = gatherer.getError();
	if(error == null)
	    return new ColoredText("Log in!");
	else
	    return new ColoredText(error, ColorRGBA.Red);
    }
    
    private static void login(Panel login, Panel register) {
	LoginComponentGatherer gatherer = new LoginComponentGatherer(true, login, register);
	Map<String, String> map = gatherer.gatherStrings(login, register);
	if(map.isEmpty()) return;
	String[] server = map.get("Server").split(":");
	String hostname = server[0];
	String port = server.length > 1 ? server[1] : "41342";
	if(register == null)
	    doLogin(hostname, port, map);
	else
	    doRegister(hostname, port, map);
    }
    
    private static void doLogin(String hostname, String port, final Map<String, String> map) {
	ClientManager.connect(hostname, port).then(new Effect<PWGClient>() {
	    @Override public void execute(PWGClient client) {
		final String username = map.get("Username");
		final String password = map.get("Password");
		client.transaction(TransactionType.LOGIN, username, password)
			.then(new Runnable() {
			    @Override public void run() {
				LoginPrompt.LOCAL_USERNAME = username;
				ChatChannel.chatChannel();
			    }
			}).onError(new Effect<Throwable>() {
			    @Override public void execute(Throwable input) {
				Notifier.errorNotify(input.getLocalizedMessage());
			    }
			})
			;
	    }
	});
    }
    
    private static void doRegister(String hostname, String port, final Map<String, String> map) {
	ClientManager.connect(hostname, port).then(new Effect<PWGClient>() {
	    @Override public void execute(PWGClient client) {
		final String username = map.get("Username");
		final String password = map.get("Password");
		final String email = map.get("Email");
		client.transaction(TransactionType.BEGIN_REGISTRATION, username, password, email)
			.then(new Runnable() {
			    @Override public void run() {
				LoginPrompt.authPrompt();
			    }
			}).onError(new Effect<Throwable>() {
			    @Override public void execute(Throwable input) {
				Notifier.errorNotify(input.getLocalizedMessage());
			    }
			})
			;
	    }
	});
    }
    
}
