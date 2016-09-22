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

package io.riddles.javainterface.engine;

import java.util.ArrayList;
import java.util.logging.Logger;

import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.javainterface.game.processor.AbstractProcessor;
import io.riddles.javainterface.io.IO;

/**
 * io.riddles.javainterface.engine.platformInterface - Created on 22-9-16
 *
 * Platform interface
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public abstract class AbstractPlatformHandler {

    protected final static Logger LOGGER = Logger.getLogger(AbstractPlatformHandler.class.getName());

    protected IO ioHandler;
    protected AbstractProcessor processor;
    protected ArrayList<AbstractPlayer> players;

    protected String[] botInputFiles;

    protected AbstractPlatformHandler(IO ioHandler) {
        this.ioHandler = ioHandler;
        this.players = new ArrayList<>();
    }

    protected void setProcessor(AbstractProcessor processor) {
        this.processor = processor;
    }

    protected void addPlayer(AbstractPlayer player) {
        this.players.add(player);
    }

    protected void setBotInputFiles(String[] botInputFiles) {
        this.botInputFiles = botInputFiles;
    }

    protected abstract void parseArguments(String args[]) throws TerminalException;

    protected abstract void setBotIoHandler(AbstractPlayer player);

    protected abstract void sendDefaultSettings(AbstractPlayer player, String[] ids);

    protected abstract void finish(String playedGame);
}
