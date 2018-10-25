package com.patchworkgalaxy.display.ui.defs.login;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.patchworkgalaxy.Definitions;
import com.patchworkgalaxy.Efforts;
import com.patchworkgalaxy.display.oldui.ColoredText;
import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.controller.Property;
import com.patchworkgalaxy.display.ui.descriptors.ComponentCallback;
import com.patchworkgalaxy.display.ui.descriptors.ComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardComponentDescriptor;
import com.patchworkgalaxy.display.ui.util.StandardPanelDescriptor;
import com.patchworkgalaxy.display.ui.util.action.CompoundAction;
import com.patchworkgalaxy.display.ui.util.action.TeardownPanelAction;
import com.patchworkgalaxy.display.ui.util.action.UpdateAllComponentsAction;
import com.patchworkgalaxy.display.ui.util.action.WriteAction;

class RegisterPD extends StandardPanelDescriptor {
    
    private final Component _registerButton, _passwordInput;
    
    private static Panel _panel;
    
    RegisterPD(Component registerButton, Component passwordInput) {
	super(getEmailCD(), getConfirmCD(), getMetaCD());
	_passwordInput = passwordInput;
	_registerButton = registerButton;
    }
    
    private static ComponentDescriptor getEmailCD () {
	return new LoginTextInputCD("Email", 2, 0, false)
		.setOpacity(0)
		.setZIndex(Definitions.Z_INDEX_MED + 48)
		.addCallback(new CompoundAction(
		    new WriteAction(LoginTextInputCD.getCenter(1, 0), Property.CENTER),
		    new WriteAction(1f, Property.OPACITY)
		).asCallback(ComponentCallback.Type.UPDATE))
		.setTooltip("A confirmation email will be delivered to this address")
		.setText(new ColoredText("Required", ColorRGBA.Red))
		;
    }
    
    private static ComponentDescriptor getConfirmCD () {
	return new LoginTextInputCD("Confirm Password", 2, 0, true)
		.setOpacity(0)
		.setZIndex(Definitions.Z_INDEX_MED + 40)
		.addCallback(new CompoundAction(
		    new WriteAction(LoginTextInputCD.getCenter(3, 0), Property.CENTER),
		    new WriteAction(1f, Property.OPACITY)
		).asCallback(ComponentCallback.Type.UPDATE))
		.setTooltip("Type your password again to confirm it")
		.setText(new ColoredText("Type your password again to confirm it", ColorRGBA.Green))
		;
    }
    
    private static ComponentDescriptor getMetaCD() {
	return new StandardComponentDescriptor(Vector2f.ZERO, Vector2f.ZERO)
		.addCallback(new TeardownPanelAction().asCallback(ComponentCallback.Type.MOUSE_CLICK))
		.setHotkey(Definitions.KEY_ESC)
		.setZIndex(Definitions.Z_INDEX_ULTRAHIGH)
		;
    }
    
    @Override public void onShow(Panel panel) {
	if(_panel != null) _panel.hide(); //better safe than sorry
	Efforts.submit(
		new WriteAction(LoginTextInputCD.getCenter(2, 0), Property.CENTER).asCallable(_passwordInput)
		).then(new UpdateAllComponentsAction(panel).asCallable(null));
	_registerButton.write(new ColoredText("Cancel"), Property.TEXT);
	_panel = panel;
    }
    
    @Override public void onHide(Panel panel) {
	_passwordInput.write(LoginTextInputCD.getCenter(1, 0), Property.CENTER);
	_registerButton.write(new ColoredText("Register"), Property.TEXT);
	_panel = null;
    }
    
    static boolean hide() {
	if(_panel != null) {
	    _panel.hide();
	    return true;
	}
	return false;
    }
    
}
