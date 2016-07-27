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
import io.riddles.bookinggame.game.data.BookingGameBoard
import io.riddles.bookinggame.game.data.Coordinate
import io.riddles.bookinggame.game.data.Enemy
import io.riddles.bookinggame.game.data.MoveType
import io.riddles.bookinggame.game.move.AlwaysRightEnemyAI
import io.riddles.bookinggame.game.move.RandomEnemyAI
import io.riddles.bookinggame.game.state.BookingGameState
import io.riddles.javainterface.game.player.AbstractPlayer
import io.riddles.javainterface.io.IOHandler
import org.json.JSONObject
import spock.lang.Specification

/**
 * io.riddles.bookinggame.engine.BookingGameEngineSpec - Created on 8-6-16
 *
 * [description]
 *
 * @author joost
 */
class BookingGameEngineSpec extends Specification {

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
        String finalBoard;

        TestEngine(IOHandler ioHandler) {
            super();
            this.ioHandler = ioHandler;
        }

        TestEngine(String wrapperFile, String[] botFiles) {
            super(wrapperFile, botFiles)
        }

        IOHandler getIOHandler() {
            return this.ioHandler;
        }

        void setup() {
            super.setup();
            this.processor.enemyAI = new AlwaysRightEnemyAI();
        }

        void finish() {
            super.finish();
        }

        /**
         * Does everything needed to send the GameWrapper the results of
         * the game.
         * @param initialState The start-of-game state
         */
        @Override
        protected void finish(BookingGameState initialState) {
            System.out.println("FINISH");
            this.finalBoard = initialState.getBoard().toRepresentationString(players, initialState)
            super.finish(initialState);
        }

        @Override
        protected BookingGameState getInitialState() {
            BookingGameState s = new BookingGameState();
            BookingGameBoard b = new BookingGameBoard(20, 11);
            b.initialiseFromString(standardBoard, 20, 11);
            s.setBoard(b);
            s.addEnemy(new Enemy(new Coordinate(9, 5), MoveType.LEFT));
            s.addEnemy(new Enemy(new Coordinate(1, 7), MoveType.UP));
            s.addEnemy(new Enemy(new Coordinate(12, 7), MoveType.RIGHT));
            return s;
        }

        @Override
        protected void initialiseData() {
            this.startCoordinates = new Coordinate[4];
            this.startCoordinates[0] = new Coordinate(1, 5);
            this.startCoordinates[1] = new Coordinate(19, 3);
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

        void finish() {
            super.finish();
        }
    }

    def engine = new TestEngine(Mock(IOHandler));


    def "test engine setup"() {
        println("test engine setup")

        setup:
        engine.getIOHandler().getNextMessage() >>> ["initialize", "bot_ids 1,2", "start"]

        when:
        engine.setup()

        then:
        1 * engine.getIOHandler().sendMessage("ok")

        expect:
        engine.getPlayers().size() == 2
        engine.getPlayers().get(0).getId() == 1
        engine.getPlayers().get(1).getId() == 2
    }

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

        engine.configuration.get("max_rounds") == 40
        engine.configuration.get("weapon_paralysis_duration") == 10
    }


    def "test running of standard game"() {
        println("test running of standard game")

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/resources/wrapper_input.txt"
        botInputs[0] = "./test/resources/bot1_input.txt"
        botInputs[1] = "./test/resources/bot2_input.txt"

        def engine = new StandardTestEngine(wrapperInput, botInputs)

        when:
        engine.run()

        then:
        1 * engine.finish()

        expect:
        engine.configuration().get("max_rounds") == 5
    }

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

        1 * engine.finish()

        expect:
        engine.configuration.get("max_rounds") == 40
        engine.finalBoard == "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,C,C,.,.,x,.,C,.,C,C,C,C,.,x,2,.,.,.,x,x,C,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x,x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x,x,x,x,x,x,x,x,x,x,.,.,x,x,.,x,x,.,x,.,x,x,.,.,.,.,.,.,x,.,.,.,E,x,.,.,.,.,.,.,x,x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x,x,E,x,.,.,.,1,.,.,.,.,.,.,.,C,.,E,x,.,x,x,.,x,x,.,x,.,x,x,x,x,x,x,C,x,.,x,x,.,x,x,.,.,.,.,x,.,.,.,.,.,.,.,C,x,C,.,.,C,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }
}