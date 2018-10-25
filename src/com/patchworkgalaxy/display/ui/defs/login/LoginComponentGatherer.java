package com.patchworkgalaxy.display.ui.defs.login;

import com.patchworkgalaxy.display.ui.controller.Component;
import com.patchworkgalaxy.display.ui.controller.Panel;
import com.patchworkgalaxy.display.ui.util.gather.AlphanumValidator;
import com.patchworkgalaxy.display.ui.util.gather.CombinedValidator;
import com.patchworkgalaxy.display.ui.util.gather.ComponentGatherer;
import com.patchworkgalaxy.display.ui.util.gather.ComponentValidator;
import com.patchworkgalaxy.display.ui.util.gather.LengthValidator;
import java.util.regex.Pattern;

final class LoginComponentGatherer extends ComponentGatherer {
    
    private String _password;
    private String _error;
    
    LoginComponentGatherer(boolean selects, Panel login, Panel register) {
	gathers("Username", new CombinedValidator(selects, USERNAME_VALIDATOR));
	if(register != null)
	    gathers("Email", new CombinedValidator(selects, EMAIL_VALIDATOR));
	gathers("Password", new CombinedValidator(selects, PASSWORD_VALIDATOR));
	if(register != null)
	    gathers("Confirm Password", new CombinedValidator(selects, CONFIRM_PASSWORD_VALIDATOR));
	gathers("Server", null);
    }
    
    String getError() {
	return _error;
    }
    
    private static final Pattern EMAIL = Pattern.compile("^\\w+@\\w+\\.\\w+$");
    
    private final ComponentValidator USERNAME_VALIDATOR =
	    new CombinedValidator(new AlphanumValidator(false), LengthValidator.min(6, false)) {
		@Override protected void onInvalid(Component component) {
		    if(component.getInputText().length() <= 6)
			_error = "Username should be at least six characters";
		    else
			_error = "Only letters, numbers, and underscores are allowed in usernames";
		}
	    };
    
    private final ComponentValidator EMAIL_VALIDATOR = new ComponentValidator(false) {
	@Override protected boolean validate(Component component) {
	    String text = component.getInputText();
	    if(text == null || text.isEmpty()) return false;
	    return EMAIL.matcher(text).find();
	}
	@Override protected void onInvalid(Component component) {
	    _error = "Please enter a valid email address";
	}
    };
    
    private final ComponentValidator PASSWORD_VALIDATOR = new LengthValidator(6, Integer.MAX_VALUE, false) {
	@Override protected boolean validate(Component component) {
	    boolean result = super.validate(component);
	    if(result)
		_password = component.getInputText();
	    return result;
	}
	@Override protected void onInvalid(Component component) {
	    _error = "Password should be at least six characters";
	}
    };
    
    private final ComponentValidator CONFIRM_PASSWORD_VALIDATOR = new ComponentValidator(false) {
	@Override protected boolean validate(Component component) {
	    String text = component.getInputText();
	    if(text == null) return false;
	    return text.equals(_password);
	}
	@Override protected void onInvalid(Component component) {
	    _error = "Password mismatch";
	}
    };
    
}
