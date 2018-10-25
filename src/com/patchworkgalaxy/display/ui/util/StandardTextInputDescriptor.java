package com.patchworkgalaxy.display.ui.util;

import com.patchworkgalaxy.display.ui.descriptors.TextInputDescriptor;

public class StandardTextInputDescriptor implements TextInputDescriptor {
    
    private final String _prefix, _prompt, _initialText;
    private boolean _password;
    private boolean _eraseOnLostFocus;
    
    public StandardTextInputDescriptor() {
	_prefix = _prompt = _initialText = "";
    }
    
    public StandardTextInputDescriptor(String prefix, String prompt, String initialText) {
	_prompt = prompt;
	_prefix = prefix;
	_initialText = initialText;
    }
    
    public StandardTextInputDescriptor(String prefix, String prompt) {
	_prompt = prompt;
	_prefix = prefix;
	_initialText = null;
    }
    
    public StandardTextInputDescriptor setPassword() {
	_password = true;
	return this;
    }

    public StandardTextInputDescriptor setEraseOnLostFocus() {
	_eraseOnLostFocus = true;
	return this;
    }
    
    @Override public String getPrefix() {
	return _prefix;
    }

    @Override public String getPrompt() {
	return _prompt;
    }

    @Override public boolean isPassword() {
	return _password;
    }

    @Override public boolean isEraseOnLostFocus() {
	return _eraseOnLostFocus;
    }

    @Override public String getInitialText() {
	return _initialText;
    }
    
    
    
}
