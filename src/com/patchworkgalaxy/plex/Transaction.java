package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Transaction implements Iterable<Transaction> {
    
    private final String _signature;
    private final Mode _mode;
    private final String _variable, _value;
    private final Transaction _next;
    
    public static enum Mode {
	CREATE, WRITE, RELEASE;
    }
    
    private Transaction(String signature, String variable, String value, Mode mode, Transaction next) {
	_signature = signature;
	_variable = variable;
	_value = value;
	_mode = mode;
	_next = next;
    }
    
    private Transaction(Transaction base, Transaction next) {
	_signature = base._signature;
	_variable = base._variable;
	_value = base._value;
	_mode = base._mode;
	_next = next;
    }
    
    public Transaction(Transaction copyOf) {
	Transaction next = copyOf._next == null ? null : new Transaction(copyOf._next);
	_signature = copyOf._signature;
	_variable = copyOf._variable;
	_value = copyOf._value;
	_mode = copyOf._mode;
	_next = next;
    }
    
    public static Transaction getMakeRecordTransaction(String signature, String type) {
	return new Transaction(signature, type, "", Mode.CREATE, null);
    }
    
    public static Transaction getWriteTransaction(String signature, String variable, String value) {
	return new Transaction(signature, variable, value, Mode.WRITE, null);
    }
    
    public static Transaction getCombinedTransaction(List<Transaction> transactions) {
	return combine(transactions.toArray(new Transaction[transactions.size()]));
    }
    
    static Transaction combine(Transaction... transactions) {
	int i = transactions.length;
	while(--i >= 1)
	    transactions[i-1] = new Transaction(transactions[i-1], transactions[i]);
	return transactions[0];
    }
    
    public Mode getMode() {
	return _mode;
    }
    
    public String getSignature() {
	return _signature;
    }
    
    public String getValue() {
	return _mode == Mode.WRITE ? _value : null;
    }
    
    public String getVariable() {
	return _mode == Mode.WRITE ? _variable : null;
    }
    
    public String getType() {
	return _mode == Mode.CREATE ? _variable : null;
    }
    
    void submit(AbstractContext context) throws PlexException {
	context.accept(this);
    }

    @Override
    public Iterator<Transaction> iterator() {
	return new Iterator<Transaction>() {
	    private Transaction _transaction = Transaction.this;
	    @Override
	    public boolean hasNext() {
		return _transaction != null;
	    }
	    @Override
	    public Transaction next() {
		try {
		    Transaction result = _transaction;
		    _transaction = result._next;
		    return result;
		} catch(NullPointerException e) { throw new NoSuchElementException(); }
	    }
	    @Override
	    public void remove() {
		throw new UnsupportedOperationException("Transactions are immutable");
	    }
	};
    }
    
}
