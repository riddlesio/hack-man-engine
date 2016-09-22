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

package io.riddles.javainterface.theaigames;

import com.mongodb.BasicDBObject;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import io.riddles.javainterface.engine.AbstractPlatformHandler;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.javainterface.io.IO;
import io.riddles.javainterface.theaigames.connections.Amazon;
import io.riddles.javainterface.theaigames.connections.Database;
import io.riddles.javainterface.theaigames.io.AIGamesBotIOHandler;

/**
 * io.riddles.javainterface.theaigames.TheAIGamesHandler - Created on 22-9-16
 *
 * Handles stuff (like saving the game) that needs to happen for the TheAIGames
 * platform only
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class TheAIGamesHandler extends AbstractPlatformHandler {

    private final String RUN_BOT_COMMAND = "/opt/aigames/scripts/run_bot.sh";

    private ArrayList<String> botCommands;
    private ArrayList<String> mongoIds;
    private String aigamesIdString;

    public TheAIGamesHandler(IO ioHandler) {
        super(ioHandler);
        this.botCommands = new ArrayList<>();
        this.mongoIds = new ArrayList<>();
    }

    @Override
    public void parseArguments(String[] args) throws TerminalException {
        this.aigamesIdString = args[0];

        if (isLocalMatch()) {
            for (String arg : args) {
                this.mongoIds.add("_id");
                this.botCommands.add(arg);
            }
            return;
        }

        if (args.length % 2 == 0) {
            throw new TerminalException("AIGames engine: Wrong number of argument provided.");
        }

        int halfIndex = (args.length - 1) / 2;
        for (int i = 1; i <= halfIndex; i++) {
            this.mongoIds.add(args[i]);
            this.botCommands.add(args[i + halfIndex]);
        }
    }

    @Override
    protected void setBotIoHandler(AbstractPlayer player) {
        int id = player.getId();

        try {
            Process botProcess = createBotProcess(id);
            String botMongoId = this.mongoIds.get(id);

            AIGamesBotIOHandler ioHandler = new AIGamesBotIOHandler(
                    botProcess, botMongoId);
            player.setIoHandler(ioHandler);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
            finish("{}");
        }
    }

    @Override
    protected void sendDefaultSettings(AbstractPlayer player, String[] ids) {
        String playerNames = "";
        String connector = "";
        for (String id : ids) {
            playerNames += String.format("%splayer%s", connector, id);
            connector = ",";
        }

        AIGamesBotIOHandler bot = (AIGamesBotIOHandler) player.getIoHandler();
        bot.sendMessage(String.format("settings player_names %s", playerNames));
        bot.sendMessage(String.format("settings your_bot player%d", player.getId()));
        bot.sendMessage(String.format("settings timebank %d", bot.getMaxTimebank()));
        bot.sendMessage(String.format("settings time_per_move %d", bot.getTimePerMove()));
    }

    @Override
    protected void finish(String playedGame) {
        for (AbstractPlayer player : this.players) {
            ((AIGamesBotIOHandler) player.getIoHandler()).finish();
        }

        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        saveToAIGames(playedGame);

        System.err.println("Done.");
        System.exit(0);
    }

    private Process createBotProcess(int id) throws IOException {
        String command;

        if (isLocalMatch()) {
            command = this.botCommands.get(id);
        } else {
            command = String.format(
                    "%s aiplayer%d %s", RUN_BOT_COMMAND, id + 1, this.botCommands.get(id));
        }

        System.out.println(command);

        return Runtime.getRuntime().exec(command);
    }

    private void saveToAIGames(String playedGame) {
        if (isLocalMatch()) {
            System.out.println(playedGame);
            return;
        }

        int score = (int) this.processor.getScore();
        AbstractPlayer winner = this.processor.getWinner();
        String gamePath = "games/" + this.aigamesIdString;

        BasicDBObject errors = new BasicDBObject();
        BasicDBObject dumps = new BasicDBObject();

        ObjectId winnerId = null;
        if (winner != null) {
            System.err.println("winner: " + winner.getName());
            winnerId = new ObjectId(this.mongoIds.get(winner.getId() - 1));
        } else {
            System.err.println("winner: draw");
        }

        System.err.println("Saving the game...");

        // Save visualization file to Amazon
        Amazon.connectToAmazon();
        String visualizationFile = Amazon.saveToAmazon(playedGame, gamePath + "/visualization");

        // Save errors and dumps to Amazon and create object for database
        for (AbstractPlayer player : this.players) {
            String botId = this.mongoIds.get(player.getId() - 1);
            AIGamesBotIOHandler ioHandler = (AIGamesBotIOHandler) player.getIoHandler();

            String errorPath = String.format("%s/bot%dErrors", gamePath, player.getId());
            String dumpPath = String.format("%s/bot%dDump", gamePath, player.getId());

            String errorLink = Amazon.saveToAmazon(ioHandler.getStderr(), errorPath);
            String dumpLink = Amazon.saveToAmazon(ioHandler.getDump(), dumpPath);

            errors.append(botId, errorLink);
            dumps.append(botId, dumpLink);
        }

        // store everything in the database
        Database.connectToDatabase();
        Database.storeGameInDatabase(this.aigamesIdString, winnerId,
                score, visualizationFile, errors, dumps);
    }

    private boolean isLocalMatch() {
        try {
            new ObjectId(this.aigamesIdString);
        } catch (IllegalArgumentException ex) {
            return true;
        }

        return false;
    }
}
