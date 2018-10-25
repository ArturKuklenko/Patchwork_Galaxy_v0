package com.patchworkgalaxy.display.ui.defs.mainmenu;

import com.patchworkgalaxy.LocalServerLogin;
import com.patchworkgalaxy.display.oldui.uidefs.LoginPrompt;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.defs.login.LoginPD;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.NoOpAction;

class MainMenuClickActions {
    private MainMenuClickActions() {}
    private static final Action DUMMY = new NoOpAction();
    
    private static final Action LOGIN = new Action() {
	@Override public void act(Component actOn) {
	    UI.Instance.showPanel(new LoginPD());
	    //LoginPrompt.loginPrompt();
	}
    };
    
    private static final Action LOCAL = new Action() {
	@Override public void act(Component actOn) {
	    LoginPrompt.LOCAL_USERNAME = "Operator"; //ick
	    LocalServerLogin.localServerLogin();
	}
    };
    
    static Action getAction(int x, int y) {
	Action result;
	try {
	    result = actions[x][y];
	} catch(ArrayIndexOutOfBoundsException e) {
	    result = DUMMY;
	}
	if(result == null) result = DUMMY;
	return result;
    }
    
    static Action[][] actions = new Action[][] {
	new Action[] { DUMMY, DUMMY, DUMMY },
	new Action[] { LOGIN, LOCAL },
	new Action[] { DUMMY, DUMMY, DUMMY },
	new Action[] { DUMMY, DUMMY, DUMMY }
    };
    
}
