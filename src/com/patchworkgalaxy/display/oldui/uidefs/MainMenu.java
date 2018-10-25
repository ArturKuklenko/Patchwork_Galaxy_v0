package com.patchworkgalaxy.display.oldui.uidefs;

import com.patchworkgalaxy.display.oldui.UX2D;
import com.patchworkgalaxy.display.ui.UI;
import com.patchworkgalaxy.display.ui.defs.mainmenu.MainMenuPD;

public class MainMenu {
    
    private MainMenu() { }
    
    public static void mainMenu() {
	UX2D.getInstance().killAllControls();
	UI.Instance.showPanel(new MainMenuPD());
    }
    
}
