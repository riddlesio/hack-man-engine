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

package io.riddles.bookinggame.game.processor;

import java.util.ArrayList;
import java.util.Iterator;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.data.*;
import io.riddles.bookinggame.game.move.*;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.bookinggame.BookingGame;
import io.riddles.javainterface.game.processor.AbstractProcessor;

/**
 * io.riddles.catchfrauds.interface.BookingGameProcessor - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGameProcessor extends AbstractProcessor<BookingGamePlayer, BookingGameState> {

    private ArrayList<Record> records;
    private int roundNumber;
    private boolean gameOver;

    public EnemyAI enemyAI = new ChaseEnemyAI();

    public BookingGameProcessor(ArrayList<BookingGamePlayer> players) {
        super(players);
        this.gameOver = false;
    }

    @Override
    public void preGamePhase() {

    }

    @Override
    public BookingGameState playRound(int roundNumber, BookingGameState state) {
        this.roundNumber = roundNumber;
        LOGGER.info(String.format("Playing round %d", roundNumber));

        ArrayList<BookingGameMove> moves = new ArrayList();
        BookingGameBoard newBoard = state.getBoard();
        int oldSnippetsEaten = state.getSnippetsEaten();
        for (BookingGamePlayer player : this.players) {
            newBoard.updateComplete(players, state);
            //System.out.println(player);
            player.sendUpdate("field", player, newBoard.toStringComplete());
            String response = player.requestMove(ActionType.MOVE.toString());

            // parse the response
            BookingGameMoveDeserializer deserializer = new BookingGameMoveDeserializer(player);
            BookingGameMove move = deserializer.traverse(response);

            // create the next state
            moves.add(move);

            BookingGameLogic l = new BookingGameLogic();

            try {
                state = l.transformBoard(state, move, this.players);
            } catch (Exception e) {
                LOGGER.info(String.format("Unknown response: %s", response));
            }


            player.updateParalysis();


            // stop game if bot returns nothing
            if (response == null) {
                this.gameOver = true;
            }
        }

        BookingGameState nextState = new BookingGameState(state, moves, roundNumber);

        nextState.setBoard(newBoard);
        nextState.setRepresentationString(players);



        for (Enemy enemy : nextState.getEnemies()) {
            nextState.getBoard().updateComplete(players, nextState);
            enemy = this.enemyAI.transform(enemy, nextState);
        }

        if (roundNumber % BookingGameEngine.configuration.get("snippet_spawn_rate") == 0) {
            //System.out.println("Adding " + BookingGameEngine.configuration.get("snippet_spawn_count") + " snippet(s)");
            for (int i = 0; i < BookingGameEngine.configuration.get("snippet_spawn_count"); i++) {
                newBoard.addSnippet(newBoard.getLoneliestField(players));
                newBoard.updateComplete(players, nextState);
            }
        }

        if (oldSnippetsEaten != nextState.getSnippetsEaten()) { /* A snippet has been eaten */
            if (nextState.getSnippetsEaten() > BookingGameEngine.configuration.get("enemy_spawn_delay")) {
                //System.out.println((nextState.getSnippetsEaten() - BookingGameEngine.configuration.get("enemy_spawn_delay")));
                if ((nextState.getSnippetsEaten() - BookingGameEngine.configuration.get("enemy_spawn_delay")) % BookingGameEngine.configuration.get("enemy_spawn_rate") == 0) {
                    //System.out.println("Adding " + BookingGameEngine.configuration.get("enemy_spawn_count") + " enem(y)(ie)(s)");
                    for (int i = 0; i < BookingGameEngine.configuration.get("enemy_spawn_count"); i++)
                        nextState.addEnemy(new Enemy(newBoard.getEnemyStartField(), new RandomEnemyAI().getRandomDirection()));
                }
            }
        }



        nextState.setRepresentationString(players);
        newBoard.dump(this.players, nextState);

        return nextState;
    }

    @Override
    public boolean hasGameEnded(BookingGameState state) {
        boolean returnVal = false;
        if (getWinner() != null) returnVal = true;
        if (this.gameOver) returnVal = true;
        if (this.roundNumber >= BookingGameEngine.configuration.get("max_rounds")) returnVal = true;
        return returnVal;
    }

    @Override
    public BookingGamePlayer getWinner() {
        int playersWithSnippets = 0;

        /* This is double work */
        for (BookingGamePlayer player : this.players) {
            if (player.getSnippets() > 0) playersWithSnippets++;
        }

        if (playersWithSnippets == 0) {
            /* All players lost their snippets, it's a draw */
            this.gameOver = true;
        } else if (playersWithSnippets == 1) {
            /* Only one player left with snippets, there's a winner */
            for (BookingGamePlayer player : this.players) {
                if (player.getSnippets() > 0) return player;
            }
        }
        if (this.roundNumber >= BookingGameEngine.configuration.get("max_rounds")) {
            /* Player with most snippets wins */
            int max = 0;
            BookingGamePlayer winner = null;
            for (BookingGamePlayer player : this.players) {
                if (player.getSnippets() > max) {
                    max = player.getSnippets();
                    winner = player;
                }
            }
            int differences = 0;
            int prevSnippets = Integer.MIN_VALUE;
            for (BookingGamePlayer player : this.players) {
                if (player.getSnippets() != prevSnippets) {
                    differences++;
                }
                prevSnippets = player.getSnippets();
            }
            if (differences > 1) { /* Not a draw */
                return winner;
            }
        }
        return null;
    }

    @Override
    public double getScore() {
        return 0;
    }

}
