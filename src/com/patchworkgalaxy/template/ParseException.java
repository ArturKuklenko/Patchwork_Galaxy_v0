package com.patchworkgalaxy.template;

public class ParseException extends Exception {

    public ParseException(String message) {
	super(new StringBuilder().append("In ").append(Parser.currentFile).append(", line ").append(Parser.currentLine).append(":\n").append(message).toString());
    }

    public ParseException(Throwable cause) {
	super(new StringBuilder().append("In ").append(Parser.currentFile).append(", line ").append(Parser.currentLine).append(":\n").toString(), cause);
    }
    
}
