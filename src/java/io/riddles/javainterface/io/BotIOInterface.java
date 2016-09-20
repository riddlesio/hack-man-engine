package io.riddles.javainterface.io;

/**
 * io.riddles.javainterface.io.BotIOInterface - Created on 15-9-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public interface BotIOInterface {

    void sendMessage(String message);

    String sendRequest(String request);

    void sendWarning(String warning);
}
