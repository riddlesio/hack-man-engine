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

import io.riddles.bookinggame.game.data.BookingGameBoard;
import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.bookinggame.game.data.Enemy;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.javainterface.game.state.AbstractState;

import java.util.ArrayList;

/**
 * io.riddles.bookinggame.game.state.BookingGameState - Created on 2-6-16
 *
 * [description]
 *
 * @author joost
 */
public class BookingGameState extends AbstractState<BookingGameMove> {

    private BookingGameBoard board;
    private ArrayList<Enemy> enemies;
    private String errorMessage;
    private String representationString;

    public BookingGameState() {
        super();
        this.enemies = new ArrayList<Enemy>();
    }

    public BookingGameState(BookingGameState previousState, BookingGameMove move, int roundNumber) {
        super(previousState, move, roundNumber);
        this.enemies = previousState.getEnemies();
    }

    public BookingGameState(BookingGameState previousState, ArrayList<BookingGameMove> moves, int roundNumber) {
        super(previousState, moves, roundNumber);
        this.enemies = previousState.getEnemies();
    }

    public BookingGameBoard getBoard() {
        return this.board;
    }
    public void setBoard(BookingGameBoard b) {
        this.board = b;
    }

    public void addEnemy(Enemy e) {
        System.out.println("adding enemy " + e.getCoordinate());
        enemies.add(e);
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public void setEnemies(ArrayList<Enemy> e) { this.enemies = e; }

    public void setRepresentationString(ArrayList<BookingGamePlayer> players) {
        this.representationString = this.board.toRepresentationString(players, this);
    }

    public String getRepresentationString() {
        return this.representationString;
    }
}
