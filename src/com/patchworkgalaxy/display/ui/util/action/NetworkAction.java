package com.patchworkgalaxy.display.ui.util.action;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.client.ClientManager;
import com.patchworkgalaxy.network.transaction.TransactionType;

public class NetworkAction extends Action {
    
    private final TransactionType _nettype;
    private final String[] _wiring;
    
    public NetworkAction(TransactionType nettype, String... wiring) {
	nettype.parse(wiring); //verify that the wiring is correct
	_nettype = nettype;
	_wiring = wiring;
    }

    @Override public void act(Component actOn) {
	Panel panel = actOn.getPanel();
	int i = _wiring.length;
	String[] inputs = new String[i];
	while(--i >= 0)
	    inputs[i] = panel.readComponent(_wiring[i]);
	ClientManager.client().transaction(_nettype, inputs);
    }
    
}
