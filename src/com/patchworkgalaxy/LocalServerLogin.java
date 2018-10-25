package com.patchworkgalaxy;

import com.jme3.math.ColorRGBA;
import com.jme3.system.JmeContext;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.uidefs.ChatChannel;
import com.patchworkgalaxy.display.oldui.uidefs.Notifier;
import com.patchworkgalaxy.network.oldmessages.ProtocolCallbacks;
import com.patchworkgalaxy.network.oldmessages.ProtocolMessage;
import com.patchworkgalaxy.network.server.ChatMessage;
import com.patchworkgalaxy.network.server.PatchworkGalaxyServer;
import com.patchworkgalaxy.network.server.account.AccountManager;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Transitional.
 */
public class LocalServerLogin {
    private LocalServerLogin() {}

    public static void localServerLogin() {
	final ProtocolCallbacks joinCallback = new ProtocolCallbacks() {
	    @Override
	    public void succeed(ProtocolMessage result) {
		ChatChannel.chatChannel();
		String lanIP;
		try {
		    lanIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		    lanIP = "[unable to determine]";
		}
		String message = "Local server enabled. You are logged in with username Operator." + "\n\tUsers may connect by specifying your local IP address, which appears to be " + lanIP + "." + "\n\tIf port 41342 is forwarded, users may also connect using your world IP.";
		ClientManager.client().messageReceived(null, new ChatMessage(message));
	    }

	    @Override
	    public void fail(ProtocolMessage reason) {
		Notifier.notify(new ColoredText("Error starting local server:\n").addText(reason.getHumanMessage(), ColorRGBA.Red));
	    }
	};
	(new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    PatchworkGalaxyServer.getInstance().start(JmeContext.Type.Headless);
		    AccountManager.enableLocalBehavior();
		    //ClientManager.connect("localhost", "41342").send(new ProtocolMessage("LGINOperator;;"), joinCallback);
		    ClientManager.connect("localhost", "41342")
			    .then(new Runnable() {
				@Override public void run() {
				    ClientManager.client().send(new ProtocolMessage("LGINOperator;;"), joinCallback);
				}
			    });
		} catch (Exception e) {
		    Notifier.errorNotify("Error with local server\n\n" + e.getLocalizedMessage());
		    PatchworkGalaxyServer.getInstance().stop();
		}
	    }
	})).start();
    }    
    
}
