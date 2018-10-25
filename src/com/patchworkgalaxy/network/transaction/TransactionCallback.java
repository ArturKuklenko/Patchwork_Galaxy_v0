package com.patchworkgalaxy.network.transaction;

public interface TransactionCallback {
    
    void callback(Response response) throws TransactionException;
    
}
