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

package io.riddles.bookinggame.engine;

import java.awt.Point;
import java.security.SecureRandom;

import io.riddles.bookinggame.game.board.BookingGameBoard;

import io.riddles.bookinggame.game.processor.BookingGameProcessor;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.bookinggame.game.BookingGameSerializer;

/**
 * io.riddles.bookinggame.engine.BookingGameEngine - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class BookingGameEngine extends AbstractEngine<BookingGameProcessor,
        BookingGamePlayer, BookingGameState> {

    public static final SecureRandom RANDOM = new SecureRandom();

    private Point[] startCoordinates;

    public BookingGameEngine() {
        super();
        setDefaults();
    }

    public BookingGameEngine(String wrapperFile, String[] botFiles) {
        super(wrapperFile, botFiles);
        setDefaults();
    }

    private void setDefaults() {
        this.startCoordinates = new Point[4];

        this.startCoordinates[0] = new Point(2, 7);
        this.startCoordinates[1] = new Point(17, 7);
        this.startCoordinates[2] = new Point(2, 7);
        this.startCoordinates[3] = new Point(17, 7);

        configuration.put("maxRounds", 40);
        configuration.put("playerSnippetCount", 0);
        configuration.put("mapSnippetCount", 0);
        configuration.put("snippetSpawnRate", 4);
        configuration.put("snippetSpawnCount", 1);
        configuration.put("initialEnemyCount", 0);
        configuration.put("enemySpawnDelay", 5);
        configuration.put("enemySpawnRate", 6);
        configuration.put("enemySpawnCount", 1);
        configuration.put("enemySnippetLoss", 4);
        configuration.put("weaponParalysisDuration", 1);
        configuration.put("fieldWidth", 20);
        configuration.put("fieldHeight", 14);
        configuration.put("fieldLayout", getDefaultFieldLayout());
    }

    @Override
    protected BookingGamePlayer createPlayer(int id) {
        BookingGamePlayer player = new BookingGamePlayer(id);
        player.setCoordinate(getStartCoordinate(id));
        return player;
    }

    @Override
    protected BookingGameProcessor createProcessor() {
        for (BookingGamePlayer player : this.players) {
            player.updateSnippets(configuration.getInt("playerSnippetCount"));
        }

        return new BookingGameProcessor(this.players);
    }

    @Override
    protected void sendGameSettings(BookingGamePlayer player) {
        player.sendSetting("field_width", configuration.getInt("fieldWidth"));
        player.sendSetting("field_height", configuration.getInt("fieldHeight"));
    }

    @Override
    protected String getPlayedGame(BookingGameState initialState) {
        BookingGameSerializer serializer = new BookingGameSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    @Override
    protected BookingGameState getInitialState() {
        BookingGameState state = new BookingGameState();

        int fieldWidth = configuration.getInt("fieldWidth");
        int fieldHeight = configuration.getInt("fieldHeight");
        String fieldLayout = configuration.getString("fieldLayout");

        BookingGameBoard board = new BookingGameBoard(
                fieldWidth, fieldHeight,fieldLayout, this.players);
        board.updatePlayerMovements();

        for (int i = 0; i < configuration.getInt("mapSnippetCount"); i++) {
            board.addRandomSnippet();
        }

        state.setBoard(board);

        return state;
    }

    private String getDefaultFieldLayout() {
        return  ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.," +
                ".,x,x,x,x,x,.,x,x,x,x,x,x,.,x,x,x,x,x,.," +
                ".,x,.,.,.,.,.,x,x,x,x,x,x,.,.,.,.,.,x,.," +
                ".,x,.,x,x,x,.,.,.,x,x,.,.,.,x,x,x,.,x,.," +
                ".,.,.,.,.,x,x,x,.,x,x,.,x,x,x,.,.,.,.,.," +
                ".,x,x,x,.,x,.,.,.,.,.,.,.,.,x,.,x,x,x,.," +
                ".,.,.,x,.,x,.,x,x,x,x,x,x,.,x,.,x,.,.,.," +
                "x,x,.,x,.,.,.,x,x,x,x,x,x,.,.,.,x,.,x,x," +
                ".,.,.,x,x,x,.,x,x,x,x,x,x,.,x,x,x,.,.,.," +
                ".,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.," +
                ".,x,x,x,.,x,x,x,x,x,x,x,x,x,x,.,x,x,x,.," +
                ".,x,x,x,.,.,.,.,.,.,.,.,.,.,.,.,x,x,x,.," +
                ".,x,x,x,.,x,x,x,.,x,x,.,x,x,x,.,x,x,x,.," +
                ".,.,.,.,.,.,.,.,.,x,x,.,.,.,.,.,.,.,.,.";
    }

    private Point getStartCoordinate(int i) {
        return this.startCoordinates[i];
    }
}
