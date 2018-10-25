package com.patchworkgalaxy.display.oldui.ux;

public interface UX {
    
    /**
     * Gets one of this component's channels, potentially creating it. The
     * behavior if the channel doesn't exist is up to the implementation, but
     * generally it should either create it, except, or return null.
     * @param <T> the type of channel desired
     * @param type a Class object used to indicate the desired type
     * @param key a String key indicating the desired channel
     * @return the channel or null
     * @throws IllegalArgumentException at the implementation's discression if the channel doesn't exist
     */
    <T> UXChannel<T> getChannel(Class<T> type, String key);
    
    /**
     * Updates all of the component's channels.
     * @param tpf time per frame
     */
    void update(float tpf);
    
    UXMutex acquireMutex();
    
    void releaseMutex(UXMutex mutex);
    
}
