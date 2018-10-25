package com.patchworkgalaxy.client;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.patchworkgalaxy.network.transaction.NetTransaction;
import com.patchworkgalaxy.network.transaction.Response;
import com.patchworkgalaxy.network.transaction.TransactionException;
import java.util.concurrent.Callable;

class OutstandingTransaction implements Callable<Object>, MessageListener<Client> {
    
    private final PWGClient _client;
    private Object _result;
    private final int _id;
    
    OutstandingTransaction(PWGClient client, NetTransaction transaction) {
	_client = client;
	_id = transaction.getId();
    }

    @Override public Object call() throws Exception {
	synchronized(this) {
	    while(_result == null)
		wait();
	}
	if(_result instanceof Exception)
	    throw (Exception)_result;
	if(_result instanceof Error)
	    throw (Error)_result;
	return _result;
    }

    @Override public void messageReceived(Client source, Message m) {
	if(m instanceof Response) {
	    Response response = (Response)m;
	    if(response.getId() == _id) {
		if(response.isError())
		    _result = new TransactionException(response.getPayload().toString());
		else
		    _result = response.getPayload();
		_client.concludeTransaction(this);
		synchronized(this) {
		    notifyAll();
		}
	    }
	}
    }
    
}
