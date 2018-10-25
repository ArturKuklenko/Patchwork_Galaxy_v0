package com.patchworkgalaxy.client;

public enum ClientStatus {
    
    /**
     * The client is not connected to any server.
     */
    DISCONNECTED,
    
    /**
     * There is an outstanding connection request.
     */
    CONNECTING,
    /**
     * The client has connected to a server, but not logged in.
     */
    CONNECTED,
    
    /**
     * There is an outstanding log-in request.
     */
    AUTHENTICATING,
    /**
     * The client is fully logged in.
     */
    AUTHENTICATED;
    
}
