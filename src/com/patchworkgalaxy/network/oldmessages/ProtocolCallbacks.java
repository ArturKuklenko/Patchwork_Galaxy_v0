package com.patchworkgalaxy.network.oldmessages;

/**
 * @deprecated old-style messaging
 */
public interface ProtocolCallbacks {
    
    void succeed(ProtocolMessage result);
    
    void fail(ProtocolMessage reason);
    
}
