/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.javainterface.riddles;

import org.json.JSONObject;

import io.riddles.javainterface.engine.AbstractPlatformHandler;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.javainterface.io.BotIOHandler;
import io.riddles.javainterface.io.IO;

/**
 * io.riddles.javainterface.riddles.RiddlesHandler - Created on 22-9-16
 *
 * Handles stuff that needs to happen for the riddles platform only
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class RiddlesHandler extends AbstractPlatformHandler {

    public RiddlesHandler(IO ioHandler) {
        super(ioHandler);
    }

    @Override
    public void parseArguments(String[] args) throws TerminalException {} // no arguments need to be provided

    @Override
    protected void setBotIoHandler(AbstractPlayer player) {
        if (this.botInputFiles != null) {
            int id = player.getId();

            BotIOHandler inputFileHandler = new BotIOHandler(id, this.botInputFiles[id]);
            player.setIoHandler(inputFileHandler);
        }
    }

    @Override
    protected void sendDefaultSettings(AbstractPlayer player, String[] ids) {} // default settings sent by wrapper

    /**
     * Does everything needed to send the GameWrapper the results of
     * the game.
     * @param playedGame The played game in a string for the visualizer
     */
    @Override
    public void finish(String playedGame) {

        // let the wrapper know the game has ended
        this.ioHandler.sendMessage("end");

        // send game details
        this.ioHandler.waitForMessage("details");

        AbstractPlayer winner = this.processor.getWinner();
        String winnerId = "null";
        if (winner != null) {
            winnerId = winner.getId() + "";
        }

        JSONObject details = new JSONObject();
        details.put("winner", winnerId);
        details.put("score", this.processor.getScore());

        this.ioHandler.sendMessage(details.toString());

        // send the game file
        this.ioHandler.waitForMessage("game");
        this.ioHandler.sendMessage(playedGame);
    }
}
