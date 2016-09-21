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

import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.javainterface.game.state.AbstractState;

import java.util.ArrayList;

/**
 * io.riddles.bookinggame.game.state.BookingGameState - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class BookingGameState extends AbstractState<BookingGameMove> {

    private BookingGameBoard board;
    private int snippetsEaten;

    public BookingGameState() {
        super();
    }

    public BookingGameState(BookingGameState previousState, BookingGameMove move, int roundNumber) {
        super(previousState, move, roundNumber);
        this.snippetsEaten = previousState.snippetsEaten;
    }

    public BookingGameState(BookingGameState previousState,
                            ArrayList<BookingGameMove> moves, int roundNumber) {
        super(previousState, moves, roundNumber);
        this.snippetsEaten = previousState.snippetsEaten;
    }

    public BookingGameState createNextState(int roundNumber) {
        BookingGameState nextState = new BookingGameState(this, new ArrayList<>(), roundNumber);

        nextState.setBoard(this.getBoard().clone());
        nextState.setSnippetsEaten(this.snippetsEaten);

        return nextState;
    }

    public BookingGameBoard getBoard() {
        return this.board;
    }

    public void setBoard(BookingGameBoard b) {
        this.board = b;
    }

    public void setSnippetsEaten(int nr) {
        this.snippetsEaten = nr;
    }

    public int getSnippetsEaten() {
        return this.snippetsEaten;
    }

    public void updateSnippetsEaten(int nr) {
        this.snippetsEaten += nr;
    }
}
