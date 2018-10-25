package com.patchworkgalaxy.display.oldui.ux;

public class StandardUX extends AbstractUX {

    @Override
    public <T> UXChannel<T> getChannel(Class<T> type, String key) {
	return implyChannel(type, key, Interpolators.getValueForNullInput(type));
    }   
    
}
