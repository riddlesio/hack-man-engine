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

package io.riddles.bookinggame

import io.riddles.bookinggame.engine.BookingGameEngine
import io.riddles.bookinggame.game.board.BookingGameBoard
import io.riddles.bookinggame.game.enemy.BookingGameEnemy
import io.riddles.bookinggame.game.move.MoveType
import io.riddles.bookinggame.game.state.BookingGameState
import io.riddles.javainterface.io.IOHandler
import spock.lang.Specification
import spock.lang.Ignore

import java.awt.Point

/**
 * io.riddles.bookinggame.engine.BookingGameEngineSpec - Created on 8-6-16
 *
 * [description]
 *
 * @author joost
 */

class BookingGameEngineSpec extends Specification {

    // THIS FILE IS NOT UPDATED SINCE REWORK OF ENGINE

    class TestEngine extends BookingGameEngine {
        String standardBoard = "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                "x,C,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                "x,.,.,C,.,.,.,x,C,.,.,C,x,.,.,.,.,.,.,x," +
                "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,C,.,.,.,x,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,C,x," +
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
        int standardBoardWidth = 20;
        int standardBoardHeight = 11;
        String finalBoard;
        Point[] mockStartCoordinates;

        TestEngine(IOHandler ioHandler) {
            super();
            this.ioHandler = ioHandler;

            initMockCoordinates();
        }

        TestEngine(String wrapperFile, String[] botFiles) {
            super(wrapperFile, botFiles)
            initMockCoordinates();

        }

        void initMockCoordinates() {
            mockStartCoordinates = new Point[4];
            mockStartCoordinates[0] = new Point(1, 5);
            mockStartCoordinates[1] = new Point(19, 5);
        }

        IOHandler getIOHandler() {
            return this.ioHandler;
        }

        void setup() {
            super.setup();
            Point[] mockStartCoordinates = new Point[2];
        }

        protected void finish(BookingGameState initialState) {
            this.finalBoard = initialState.getBoard().toRepresentationString(players, initialState)
            super.finish(initialState);
        }

        @Override
        protected BookingGameState getInitialState() {
            Point[] enemySpawnPoints = new Point[4];
            enemySpawnPoints[0] = new Point(8, 7);
            enemySpawnPoints[1] = new Point(9, 7);
            enemySpawnPoints[2] = new Point(10, 7);
            enemySpawnPoints[3] = new Point(11, 7);
            BookingGameState s = new BookingGameState();
            BookingGameBoard b = new BookingGameBoard(
                    standardBoardWidth, standardBoardHeight, standardBoard, enemySpawnPoints, this.players);

            s.setBoard(b);

            return s;
        }

        @Override
        protected Point getStartCoordinate(int i) {
            return mockStartCoordinates[i];
        }
    }


    class StandardTestEngine extends BookingGameEngine {

        StandardTestEngine(IOHandler ioHandler) {
            super();
            this.ioHandler = ioHandler;
        }

        StandardTestEngine(String wrapperFile, String[] botFiles) {
            super(wrapperFile, botFiles)
        }

        IOHandler getIOHandler() {
            return this.ioHandler;
        }

        void setup() {
            super.setup();
        }
    }

    def engine = new TestEngine(Mock(IOHandler));



    //@Ignore
    def "test engine setup"() {
        println("test engine setup")

        setup:
        engine.getIOHandler().getNextMessage() >>> ["initialize", "bot_ids 1,2", "player_snippet_count 1", "start"]

        when:
        engine.setup()

        then:
        1 * engine.getIOHandler().sendMessage("ok")

        expect:
        engine.getPlayers().size() == 2
        engine.getPlayers().get(0).getId() == 1
        engine.getPlayers().get(1).getId() == 2
    }


    //@Ignore
    def "test engine configuration"() {
        println("test engine configuration")

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)

        engine.setup()

        expect:
        engine.getPlayers().size() == 2
        engine.getPlayers().get(0).getId() == 0
        engine.getPlayers().get(1).getId() == 1
        engine.getPlayers().get(0).getSnippets() == 0

        engine.configuration.getInt("maxRounds") == 200
        engine.configuration.getInt("weaponParalysisDuration") == 1
    }

//    @Ignore
    def "test running of standard game"() {
        println("test running of standard game")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new StandardTestEngine(wrapperInput, botInputs)

        engine.run()

        expect:
        engine.configuration.getInt("maxRounds") == 200
    }


}
