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
import io.riddles.bookinggame.game.data.Board;
import io.riddles.bookinggame.game.processor.BookingGameLogic;

/**
 * io.riddles.bookinggame.game.state.BookingGameState - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGameState extends AbstractState<BookingGameMove> {

    private Board board;
    private String errorMessage;

    public BookingGameState() {
        super();
    }

    public BookingGameState(BookingGameState previousState, BookingGameMove move, int roundNumber) {
        super(previousState, move, roundNumber);
    }
    public Board getBoard() {
        return this.board;
    }
    public void setBoard(Board b) {
        this.board = b;
    }

}
