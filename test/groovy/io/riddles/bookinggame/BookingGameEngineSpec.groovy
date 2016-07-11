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

package io.riddles.bookinggame.engine

import io.riddles.javainterface.io.IOHandler
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
        }

        void finish() {
            super.finish();
        }
    }

    def engine = new TestEngine(Mock(IOHandler));

    def "test engine initialization"() {

        setup:

        expect:
        1

    }

    def "test engine setup"() {

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


    def "Test running of full game with inputs from files"() {

        setup:
        String[] botInputs = new String[2]

        def wrapperInput = "./test/wrapper_input.txt"
        botInputs[0] = "./test/bot1_input.txt"
        botInputs[1] = "./test/bot2_input.txt"

        def engine = new TestEngine(wrapperInput, botInputs)

        when:
        engine.run()

        then:
        1 * engine.finish()

        expect:
        def checkPointValues = engine.processor.checkPointValues
        checkPointValues.size() == 3
        checkPointValues.get(0) == "checkpoint 1 test lalala"
        checkPointValues.get(1) == "checkpoint 2 some other checkpoint"
        checkPointValues.get(2) == "checkpoint 3 jajaja"
    }
}