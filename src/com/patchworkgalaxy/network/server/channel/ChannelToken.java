package com.patchworkgalaxy.network.server.channel;

import java.io.Serializable;

public class ChannelToken implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String _name, _password;
    
    ChannelToken(String name, String password) {
	_name = name;
	_password = password;
    }
    
    String getName() {
	return _name;
    }
    
    String getPassword() {
	return _password;
    }
    
}