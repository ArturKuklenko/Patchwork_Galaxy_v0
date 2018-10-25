package com.patchworkgalaxy.display.ui.descriptors;

public interface TextInputDescriptor {
    
    String getPrefix();
    
    String getPrompt();
    
    String getInitialText();
    
    boolean isPassword();
    
    boolean isEraseOnLostFocus();
    
}
