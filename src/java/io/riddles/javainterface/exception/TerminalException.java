package io.riddles.javainterface.exception;

/**
 * Created by joost on 7/18/16.
        */
public class TerminalException extends Exception {

    String serializedData;
    int statusCode = 1;

    public TerminalException(String message) {
        super(message);
    }

    public TerminalException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public void setStatusCode(int s) {
        this.statusCode = s;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}