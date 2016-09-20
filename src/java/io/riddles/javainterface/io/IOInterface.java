package io.riddles.javainterface.io;

import java.io.IOException;

import io.riddles.javainterface.game.player.AbstractPlayer;

/**
 * io.riddles.javainterface.io.IOInterface - Created on 15-9-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public interface IOInterface {

    void sendMessage(String message);
    void waitForMessage(String expected);
    String getNextMessage() throws IOException;
}
