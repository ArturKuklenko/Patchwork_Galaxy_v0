package com.patchworkgalaxy.display.ui.defs.login;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.oldui.uidefs.LoginPrompt;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.PanelDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardTextInputDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardTooltipDescriptor;
import com.patchworkgalaxy.display.ui.util.action.Action;
import com.patchworkgalaxy.display.ui.util.action.CancelAction;
import com.patchworkgalaxy.display.ui.util.action.CreatePanelAction;
import java.util.ArrayList;
import java.util.List;

class SecondaryLoginPD extends StandardPanelDescriptor {
    
    SecondaryLoginPD() {
	List<StandardComponentDescriptor> components = new ArrayList<>();
	components.add(new LoginButtonCD(0));
	components.add(getHeaderCD());
	components.add(getCancelButtonCD());
	components.add(getRegisterCD());
	components.add(getResetCD());
	components.add(new LoginTextInputCD("Username", 0, 0, false));
	components.add(new LoginTextInputCD("Password", 1, 0, true));
	components.add(getServerCD());
	for(StandardComponentDescriptor cd : components) {
	    cd
		    .setOpacity(0f)
		    .addTransition(.5f, Property.OPACITY)
		    ;
	}
	setComponents(components);
    }
    
    @Override public void onShow(Panel panel) {
	for(Component component : panel.getComponents())
	    component.write(1f, Property.OPACITY);
	panel.getComponent("Username").focus();
    }
    
    private static StandardComponentDescriptor getCancelButtonCD() {
	return new StandardComponentDescriptor(new Vector2f(-.3475f, .48f), new Vector2f(.05f, .066f))
		.setBackgroundImage("Interface/pwgui/buttons/xbutton.png")
		.addCallback(new CancelAction(true).asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.setZIndex(Definitions.Z_INDEX_MED + 20)
		;
    }
    
    private static StandardComponentDescriptor getHeaderCD() {
	return new StandardComponentDescriptor(new Vector2f(.05f, .475f), new Vector2f(.4f, .1f))
		.setText(new ColoredText().addText(" Patchwork Galaxy:\nM u l t i p l a y e r", ColorRGBA.Yellow))
		.setTextSize(35)
		.setTextAlignCenter()
		.setZIndex(Definitions.Z_INDEX_MED + 15)
		;
    }
    
    private static StandardComponentDescriptor getRegisterCD() {
	return new StandardComponentDescriptor(new Vector2f(-.13f, -.36f), new Vector2f(.06f, .045f))
		.setZIndex(Definitions.Z_INDEX_MED + 10)
		.setBackgroundImage("Interface/pwgui/buttons/squarebutton.png")
		.setText("Register")
		.setTextAlignCenter()
		.setTextVAlignCenter()
		.addCallback(REGISTER_ACTION.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		;
    }
    
    private static StandardComponentDescriptor getServerCD() {
	return new StandardComponentDescriptor("Server", new Vector2f(.09f, -.45f), new Vector2f(.28f, .03f))
		.setBackgroundImage("Interface/pwgui/bars/progress_bar_background.png")
		.setZIndex(Definitions.Z_INDEX_MED + 30)
		.addTransition(.25f, Property.CENTER, Property.OPACITY)
		.setTextInputDescriptor(new StandardTextInputDescriptor(" Server: ", "", "patchworkgalaxy.dyndns.org"))
		;
    }
    
    private static StandardComponentDescriptor getResetCD() {
	return new StandardComponentDescriptor(new Vector2f(.32f, .02f), new Vector2f(.05f, 0.03f))
		.setBackgroundImage("Interface/pwgui/buttons/squarebutton.png")
		.setText("Reset")
		.setTextAlignCenter()
		.setTextSize(15f)
		.setTooltipDescriptor(new StandardTooltipDescriptor("Reset your password if you forgot it"))
		.setZIndex(Definitions.Z_INDEX_MED + 47)
		.addCallback(RESET_ACTION.asCallback(ComponentCallback.Type.MOUSE_CLICK))
		;
    }
    
    private static Action REGISTER_ACTION = new Action() {
	@Override public void act(Component actOn) {
	    if(!RegisterPD.hide()) {
		PanelDescriptor pd = new RegisterPD(actOn, actOn.getPanel().getComponent("Password"));
		new CreatePanelAction(pd, "Register").act(actOn);
	    }
	}
    };
    
    private static Action RESET_ACTION = new Action() {
	@Override public void act(Component actOn) {
	    LoginPrompt.resetPrompt();
	}
    };
    
}
