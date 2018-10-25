package com.patchworkgalaxy.plex;

import com.patchworkgalaxy.plex.exceptions.PlexException;
import java.util.List;

class Test {
    
    private Test() {}
    
    public static void main(String[] args) throws Exception {
	BuildContext build = new BuildContext();
	build.defineCategory("Foobar",
		new VariableDeclaration("next", "Foobar"),
		new VariableDeclaration("scale", "int"),
		new VariableDeclaration("target", "Thingy")
		);
	build.defineCategory("Thingy",
		new VariableDeclaration("init", "void"),
		new VariableDeclaration("display", "void"),
		new VariableDeclaration("scale", "int")
		);
	build.defineCategory("Puppy",
		new VariableDeclaration("breed", "string")
		);
	Context context = build.getContext();
	context.attachMonitor(
		new VariableSpecifier("Thingy", "display"),
		new MonitorCallback() {
		    @Override public void callback(RecordView recordView) {
			System.out.println("DISPLAYING THINGY");
		    }   
		}
		);
	context.attachMonitor(
		new VariableSpecifier("Thingy", "init"),
		new MonitorCallback() {
		    @Override public void callback(RecordView recordView) {
			System.out.println("INITIALIZING THINGY");
		    }   
		}
		);
	context.attachMonitor(
		new VariableSpecifier("Thingy", "any"),
		new MonitorCallback() {
		    @Override public void callback(RecordView recordView) {
			try {
			    System.out.println("THINGY CHANGED: " + recordView.get("scale"));
			} catch(PlexException e) {}
		    }   
		}
		);
	context.attachMonitor(
		new VariableSpecifier("Foobar", "target"),
		new MonitorCallback() {
		    @Override public void callback(RecordView recordView) {
			try {
			    System.out.print("Foobar now targets Thingy with scale ");
			    System.out.print(recordView.getLinkedRecordView("target").get("scale"));
			    System.out.println();
			} catch(PlexException e) {}
		    }   
		}
		);
	log(context);
	String foobar = "[foobar signature]";
	Transaction transaction, transaction1, transaction2, transaction3;
	transaction = Transaction.getMakeRecordTransaction(foobar, "Foobar");
	context.accept(transaction);
	log(context);
	transaction = Transaction.getWriteTransaction(foobar, "scale", "7");
	context.accept(transaction);
	log(context);
	String thingy = "[thingy signature]";
	transaction1 = Transaction.getMakeRecordTransaction(thingy, "Thingy");
	transaction2 = Transaction.getWriteTransaction(thingy, "scale", "1000");
	transaction3 = Transaction.getWriteTransaction(foobar, "scale", "888");
	transaction = Transaction.combine(transaction1, transaction2, transaction3);
	context.accept(transaction);
	log(context);
	transaction = Transaction.getWriteTransaction(thingy, "scale", "-12");
	context.accept(transaction);
	log(context);
	transaction = Transaction.getWriteTransaction(foobar, "next", foobar);
	context.accept(transaction);
	transaction = Transaction.getWriteTransaction(foobar, "target", thingy);
	context.accept(transaction);
	log(context);
	transaction = Transaction.getMakeRecordTransaction("Spot", "Puppy");
	context.accept(transaction);
	transaction = Transaction.getWriteTransaction("Spot", "breed", "Shiba Inu");
	context.accept(transaction);
	log(context);
	testSerialization(context);
    }
    
    private static void testSerialization(Context context) {
	System.out.println("\n\n\n\nTesting serialization:");
	try {
	    List<String> serializedRecords = context.dump();
	    Context context2 = context.load(serializedRecords);
	    log(context2);
	    Transaction transaction = Transaction.getWriteTransaction("[thingy signature]", "scale", "707");
	    context.accept(transaction);
	    log(context2);
	    
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }
    
    private static void log(Context context) {
	System.out.println("Dumping context...");
	for(String s : context.dump())
	    System.out.println(s.equals(Definitions.NULL_CHAR) ? "----" : s);
	System.out.println("Done.");
	System.out.println();
    }
    
}