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

package io.riddles.bookinggame.game.state;

import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.javainterface.game.state.AbstractState;

/**
 * io.riddles.catchfrauds.game.state.CatchFraudsState - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGameState extends AbstractState<BookingGameMove> {

    private Boolean isFraudulent;

    public BookingGameState() {
        super();
        this.isFraudulent = null;
    }

    public BookingGameState(BookingGameState previousState,
                            BookingGameMove move, int roundNumber, boolean isFraudulent) {
        super(previousState, move, roundNumber);
        this.isFraudulent = isFraudulent;
    }

    public boolean isFraudulent() throws NullPointerException {
        if (this.isFraudulent != null) {
            return this.isFraudulent;
        }
        throw new NullPointerException("State isFraudulent is not set");
    }
}
