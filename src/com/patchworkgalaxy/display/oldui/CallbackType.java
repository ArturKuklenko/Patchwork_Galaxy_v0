package com.patchworkgalaxy.display.oldui;

public enum CallbackType {
    /**
     * The user clicks the component or presses its hotkey.
     */
    CLICK,
    
    /**
     * The mouse enters the component.
     */
    MOUSE_IN,
    
    /**
     * The mouse exits the component.
     */
    MOUSE_OUT,
    
    /**
     * Called every tick.
     */
    TICK,
    
    /**
     * An observed object {@link com.patchworkgalaxy.general.SafeObservable#notifyObservers() updates}.
     */
    OBSERVE,
    
    /**
     * Called when a text input is submitted.
     */
    SUBMIT,
    
    /**
     * Called when the component becomes visible.
     */
    INITIALIZE,
    
    /**
     * Called when the component is invalidated.
     */
    TEARDOWN,

}
