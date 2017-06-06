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
            BookingGameState s = new BookingGameState();
            BookingGameBoard b = new BookingGameBoard(standardBoardWidth, standardBoardHeight);
            b.initialiseFromString(standardBoard, standardBoardWidth, standardBoardHeight);
            s.setBoard(b);
            s.addEnemy(new BookingGameEnemy(new Point(1, 3), MoveType.RIGHT));
            s.addEnemy(new BookingGameEnemy(new Point(1, 7), MoveType.UP));
            s.addEnemy(new BookingGameEnemy(new Point(12, 7), MoveType.RIGHT));
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



    @Ignore
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


    @Ignore
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
        engine.getPlayers().get(0).getId() == 1
        engine.getPlayers().get(1).getId() == 2
        engine.getPlayers().get(0).getSnippets() == 5

        engine.configuration.getInt("maxRounds") == 40
        engine.configuration.getInt("weaponParalysisDuration") == 10
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


    @Ignore
    def "test enemy attack"() {
        println("test enemy attack")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                "x,.,.,.,.,.,.,x,.,.,.,.,x,.,.,.,.,.,.,x," +
                "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.run()

        expect:
        engine.configuration.getInt("maxRounds") == 40
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,.,.,.,.,x,C,C,C,C,C,.,.,.,x,.,.,.,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,.,.,.,.,E,.,x,.,.,.,.,x,.,.,.,.,.,.,x,x,1,x,.,x,x,.,x,x,x,x,x,x,.,x,x,2,x,.,x,x,E,x,.,.,.,.,.,.,.,.,.,.,.,.,.,E,x,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,.,.,.,x,.,.,.,C,.,.,.,.,x,.,.,.,.,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }


    @Ignore
    def "check winner when player 1 looses snippets, player 2 wins"() {
        println("check winner when player 1 looses snippets, player 2 wins")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot_goleft_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,.,.,.,.,.,.,.,.,.,.,.,x,.,.,.,.,.,.,x," +
                        "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.run()

        expect:
        //engine.getProcessor().getPlayers().get(0).getSnippets() == 10;
        engine.getProcessor().getWinner().getId() == 2;
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,.,.,.,.,x,.,.,.,.,C,.,.,.,x,.,.,.,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,1,E,.,.,.,.,.,.,.,.,.,x,.,.,.,.,.,2,x,x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,.,.,.,.,.,.,.,.,.,.,E,.,.,.,x,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check winner when player 2 looses snippets, player 1 wins"() {
        println("check winner when player 2 looses snippets, player 1 wins")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot_goleft_input.txt"
        botInputs[1] = "./test/resources/bot_goleft_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x," +
                        "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.mockStartCoordinates[0] = new Point(4, 1);
        engine.mockStartCoordinates[1] = new Point(18, 5);
        engine.run()

        expect:
        engine.getProcessor().getWinner().getId() == 1;
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,PointPointPointPointPointx,x,x,x,x,x,x,1,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,C,C,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,C,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,.,.,.,.,.,.,.,.,E,2,.,.,.,.,.,.,.,.,x,x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,.,.,.,.,.,.,.,.,.,.,.,.,.,E,x,.,x,x,.,x,x,C,x,C,x,x,x,x,x,x,.,x,.,x,x,C,x,x,.,.,.,C,x,C,C,.,.,.,.,.,.,x,.,.,.,C,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check winner when player 1 kills enemy"() {
        println("check winner when player 1 kills enemy")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot1_inputTestAttack.txt"
        botInputs[1] = "./test/resources/bot2_inputTestAttack.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,.,.,C,C,C,C,C,.,.,.,.,x,.,.,.,.,.,.,x," +
                        "x,C,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,C,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,C,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,C,C,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.run()

        expect:
        //engine.getProcessor().getPlayers().get(0).getSnippets() == 10;
        engine.getProcessor().getWinner().getId() == 2;
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,.,.,.,.,x,.,.,.,.,C,.,.,.,x,.,.,.,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,1,E,C,C,C,C,C,.,.,.,.,x,.,.,.,.,.,2,x,x,C,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,.,.,.,.,.,.,.,.,.,.,E,.,.,.,x,.,x,x,C,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,C,C,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check if a draw occurs after max_rounds and players have equal snippets"() {
        println("check if a draw occurs after max_rounds and players have equal snippets")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot_goleft_input.txt"
        botInputs[1] = "./test/resources/bot_goleft_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,.,.,.,.,.,.,.,.,.,.,.,x,.,.,.,.,.,.,x," +
                        "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.mockStartCoordinates[0] = new Point(1, 1);
        engine.mockStartCoordinates[1] = new Point(19, 5);
        engine.run()

        expect:
        engine.getPlayers().get(0).getSnippets() == 1;
        engine.getPlayers().get(1).getSnippets() == 1;
        engine.getProcessor().getWinner() == null;
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,1,.,.,.,x,.,.,C,C,C,.,.,.,x,.,C,C,C,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,C,x,x,.,x,.,.,.,C,C,C,.,.,.,.,.,.,.,.,x,C,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,.,.,.,C,C,C,.,.,.,.,E,x,2,.,.,.,.,.,x,x,.,x,C,x,x,C,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,C,C,C,C,C,C,.,.,.,.,.,.,.,E,x,C,x,x,C,x,x,C,x,C,x,x,x,x,x,x,.,x,.,x,x,C,x,x,C,C,C,C,x,C,C,C,C,C,.,.,.,x,.,C,C,C,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check player with most snippets wins after max_rounds"() {
        println("check player with most snippets wins after max_rounds")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot_goleft_input.txt"
        botInputs[1] = "./test/resources/bot_goleft_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,.,.,.,.,.,.,.,.,.,.,.,x,C,C,C,C,C,C,x," +
                        "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.mockStartCoordinates[0] = new Point(1, 1);
        engine.mockStartCoordinates[1] = new Point(19, 5);
        engine.run()

        expect:
        engine.getPlayers().get(0).getSnippets() == 1;
        engine.getPlayers().get(1).getSnippets() == 7;
        engine.getProcessor().getWinner().getId() == 2;
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,1,.,.,.,x,.,.,C,C,C,.,.,.,x,.,C,C,C,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,C,x,x,.,x,.,.,.,C,C,C,.,.,.,.,.,.,.,.,x,C,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,.,.,.,C,C,C,.,.,.,E,E,x,2,.,.,.,.,.,x,x,.,x,C,x,x,C,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,C,C,C,C,C,C,.,.,.,.,.,.,.,E,x,C,x,x,C,x,x,C,x,C,x,x,x,x,x,x,.,x,.,x,x,C,x,x,C,C,C,C,x,C,C,C,C,C,.,.,.,x,.,C,C,C,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check player collision"() {
        println("check player collision")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot_goright_input.txt"
        botInputs[1] = "./test/resources/bot_goleft_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,C,C,x," +
                        "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";

        engine.mockStartCoordinates[0] = new Point(1, 5);
        engine.mockStartCoordinates[1] = new Point(19, 5);
        engine.run()

        expect:
        engine.getPlayers().get(0).getCoordinate().getX() == 10;
        engine.getPlayers().get(0).getCoordinate().getY() == 5;

        engine.getPlayers().get(1).getCoordinate().getX() == 11;
        engine.getPlayers().get(1).getCoordinate().getY() == 5;

        engine.getProcessor().getWinner().getId() == 2;
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,C,C,C,C,x,C,.,.,C,C,.,.,.,x,C,C,C,C,x,x,C,x,x,C,x,.,x,x,x,x,x,x,.,x,.,x,x,C,x,x,C,x,C,C,.,.,.,.,.,.,.,.,.,.,.,.,x,C,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,C,x,x,C,C,.,.,.,.,.,.,E,1,2,.,.,.,.,.,.,.,x,x,C,x,C,x,x,.,x,x,x,x,x,x,.,x,x,.,x,C,x,x,E,x,C,C,.,.,.,.,.,.,.,.,.,.,.,E,x,C,x,x,C,x,x,C,x,.,x,x,x,x,x,x,.,x,.,x,x,C,x,x,C,C,C,C,x,C,.,.,.,C,.,.,.,x,.,C,C,C,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check player doesn't loose snippets when kill enemy"() {
        println("check player doesn't loose snippets when kill enemy")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_inputTestNoSnippets.txt"
        botInputs[0] = "./test/resources/bot_goleft_input.txt"
        botInputs[1] = "./test/resources/bot2_inputTestAttack.txt"

        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                        "x,W,.,C,C,W,.,.,.,.,.,.,x,.,.,.,.,.,.,x," +
                        "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                        "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
        engine.mockStartCoordinates[0] = new Point(8, 5);
        engine.mockStartCoordinates[1] = new Point(19, 5);
        engine.run()

        expect:Point
        engine.getProcessor().getPlayers().get(0).getSnippets() == 3;
        engine.getProcessor().getPlayers().get(0).getWeapons() == 1;

        engine.getProcessor().getWinner().getId() == 1;


        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,.,.,.,.,x,2,.,.,.,.,.,.,.,x,.,.,.,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,1,.,.,.,.,.,.,.,.,.,.,x,.,.,.,.,.,.,x,x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,.,.,.,.,.,.,.,.,.,.,.,.,.,E,x,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Ignore
    def "check chaseEnemyAI"() {
        println("check chaseEnemyAI")

        setup:
        String[] botInputs = new String[4]

        def wrapperInput = "./test/resources/wrapper_inputTestSnippetWinner.txt"
        botInputs[0] = "./test/resources/bot1_inputTestAttack.txt"
        botInputs[1] = "./test/resources/bot2_inputTestAttack.txt"
        botInputs[2] = "./test/resources/bot2_inputTestAttack.txt"
        botInputs[3] = "./test/resources/bot2_inputTestAttack.txt"


        def engine = new TestEngine(wrapperInput, botInputs)
        engine.standardBoard =
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                        "x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x," +
                        "x,.,x,x,x,x,x,.,x,x,x,x,x,x,.,x,x,x,x,x,.,x," +
                        "x,.,x,.,.,.,.,.,x,x,x,x,x,x,.,.,.,.,.,x,.,x," +
                        "x,.,x,.,x,x,x,.,.,.,x,x,.,.,.,x,x,x,.,x,.,x," +
                        "x,.,.,.,.,.,x,x,x,.,x,x,.,x,x,x,.,.,.,.,.,x," +
                        "x,.,x,x,x,.,x,.,.,.,.,.,.,.,.,x,.,x,x,x,.,x," +
                        "x,.,.,.,x,.,x,.,x,x,x,x,x,x,.,x,.,x,.,.,.,x," +
                        "x,x,x,.,x,.,.,.,x,x,x,x,x,x,.,.,.,x,.,x,x,x," +
                        "x,.,.,.,x,x,x,.,x,x,x,x,x,x,.,x,x,x,.,.,.,x," +
                        "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                        "x,.,x,x,x,.,x,x,x,x,x,x,x,x,x,x,.,x,x,x,.,x," +
                        "x,.,x,x,x,.,.,.,.,.,.,.,.,.,.,.,.,x,x,x,.,x," +
                        "x,.,x,x,x,.,x,x,x,.,x,x,.,x,x,x,.,x,x,x,.,x," +
                        "x,.,.,.,.,.,.,.,.,.,x,x,.,.,.,.,.,.,.,.,.,x," +
                        "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
        engine.standardBoardWidth = 22;
        engine.standardBoardHeight = 16;
        engine.mockStartCoordinates[0] = new Point(8, 5);
        engine.mockStartCoordinates[1] = new Point(19, 5);
        engine.mockStartCoordinates[2] = new Point(8, 7);
        engine.mockStartCoordinates[3] = new Point(19, 7);
        //engine.getProcessor().enemyAI = new ChaseEnemyAI();

        engine.run()

        expect:
        engine.getProcessor().getPlayers().get(0).getSnippets() == 3;
        engine.getProcessor().getPlayers().get(0).getWeapons() == 1;

        engine.getProcessor().getWinner().getId() == 1;


        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,.,.,.,.,x,2,.,.,.,.,.,.,.,x,.,.,.,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,1,.,.,.,.,.,.,.,.,.,.,x,.,.,.,.,.,.,x,x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,.,.,.,.,.,.,.,.,.,.,.,.,.,E,x,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }
}
