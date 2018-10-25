package com.patchworkgalaxy.network.transaction;

import com.patchworkgalaxy.display.oldui.uidefs.Notifier;

public abstract class AbstractTransactionCallback<T> implements TransactionCallback {

    private final String _name;
    private final Class<T> _expectedPayloadClass;
    
    public AbstractTransactionCallback(String name, Class<T> expectedPayloadClass) {
	_name = name;
	_expectedPayloadClass = expectedPayloadClass;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void callback(Response response) throws TransactionException {
	Object payload = response.getPayload();
	if(!response.isSuccess()) {
	    Notifier.notify(payload.toString());
	    return;
	}
	if(!_expectedPayloadClass.isInstance(payload)) {
	    StringBuilder sb = new StringBuilder("Incompatible payload: ")
		    .append(payload.getClass().getCanonicalName())
		    .append(" vs ")
		    .append(_expectedPayloadClass.getCanonicalName())
		    .append(" in AbstractTransactionCallback " ).append(_name);
	    throw new TransactionException(sb.toString());
	}
	callbackImpl((T)payload);
    }
    
    protected abstract void callbackImpl(T responsePayload);
    
}
