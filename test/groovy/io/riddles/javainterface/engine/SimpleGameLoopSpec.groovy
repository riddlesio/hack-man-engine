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

package io.riddles.javainterface.engine

import io.riddles.javainterface.game.processor.AbstractProcessor
import io.riddles.javainterface.game.state.AbstractState
import spock.lang.Specification

/**
 * io.riddles.javainterface.engine.SimpleGameLoopSpec - Created on 7-6-16
 *
 * [description]
 *
 * @author jim
 */
class SimpleGameLoopSpec extends Specification {

    def "Run the game for 0 rounds"() {
        println "SimpleGameLoopSpec"

        setup:
        def loop = new SimpleGameLoop()
        def initialState = Mock(AbstractState)
        def processor = Mock(AbstractProcessor)

        when:
        def finalState = loop.run(initialState, processor)

        then:
        1 * processor.hasGameEnded(_) >> true

        expect:
        initialState.roundNumber == 0
        finalState.equals(initialState)
    }
}
