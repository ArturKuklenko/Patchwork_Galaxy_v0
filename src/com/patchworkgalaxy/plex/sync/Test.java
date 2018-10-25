package com.patchworkgalaxy.plex.sync;

import com.patchworkgalaxy.plex.BuildContext;
import com.patchworkgalaxy.plex.Context;
import com.patchworkgalaxy.plex.MonitorCallback;
import com.patchworkgalaxy.plex.RecordView;
import com.patchworkgalaxy.plex.Transaction;
import com.patchworkgalaxy.plex.VariableDeclaration;
import com.patchworkgalaxy.plex.VariableSpecifier;
import com.patchworkgalaxy.plex.exceptions.PlexException;
import java.util.ArrayList;
import java.util.List;

class Test extends SyncContextServer {
    
    private Test() throws Exception {
	super(createContext());
    }
    
    private static Context createContext() throws Exception {
	BuildContext build = new BuildContext();
	build.defineCategory("Thingy",
		new VariableDeclaration("scale", "int")
		);
	return build.getContext();
    }
    
    private void recieveTransaction(Transaction transaction) {
	try {
	    accept(transaction);
	} catch(PlexException e) { e.printStackTrace(); }
    }
    
    private static class TestConnection implements SyncServerConnection {

	private final Test _test;
	private final TestClient _client;
	
	private TestConnection(Test test, Context context, String name) {
	    _client = new TestClient(test, context, name);
	    _test = test;
	}
	
	@Override
	public void synchronize(List<String> syncData) {
	    _client.handleSyncResponse(syncData);
	}

	@Override
	public void accept(Transaction transaction) {
	    _client.acceptFromServer(transaction);
	}
	
    }
    
    private static class TestClient extends SyncContextClient {
	private final String _name;
	private final Test _test;
	private TestClient(Test test, Context context, String name) {
	    super(context);
	    _test = test;
	    _name = name;
	    attachMonitor(
		new VariableSpecifier("Thingy", "scale"),
		new MonitorCallback() {
		    @Override
		    public void callback(RecordView recordView) {
			try {
			    System.out.println(_name + ": " + recordView.get("scale"));
			} catch(Exception e) { e.printStackTrace(); }
		    }
		}
	    );
	}

	@Override
	public void sendTransaction(Transaction transaction) {
	    _test.recieveTransaction(transaction);
	}

	@Override
	public void onSyncFailed() {
	    throw new RuntimeException();
	}

	@Override
	public void onTransactionFailed() {
	    throw new RuntimeException();
	}
	
    }
	
    public static void main(String[] args) throws Exception {
	Test test = new Test();
	TestConnection tc1 = new TestConnection(test, createContext(), "Alpha");
	TestConnection tc2 = new TestConnection(test, createContext(), "Bravo");
	TestConnection tc3 = new TestConnection(test, createContext(), "Charlie");
	test.addConnection(tc1);
	test.addConnection(tc2);
	test.addConnection(tc3);
	Transaction transaction1 = Transaction.getMakeRecordTransaction("foobar", "Thingy");
	Transaction transaction2 = Transaction.getWriteTransaction("foobar", "scale", "7");
	List<Transaction> foo = new ArrayList<>();
	foo.add(transaction1);
	foo.add(transaction2);
	Transaction transaction = Transaction.getCombinedTransaction(foo);
	test.accept(transaction);
	transaction = Transaction.getWriteTransaction("foobar", "scale", "8");
	test.accept(transaction);
	transaction = Transaction.getWriteTransaction("foobar", "scale", "12");
	tc2._client.accept(transaction);
    }
    
}
