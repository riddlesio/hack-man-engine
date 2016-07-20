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
import io.riddles.bookinggame.game.data.BookingGameBoard;
import io.riddles.bookinggame.game.data.Enemy;
import io.riddles.bookinggame.game.data.MoveType;
import io.riddles.bookinggame.game.move.*;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.bookinggame.BookingGame;
import io.riddles.bookinggame.game.data.Record;
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

    private EnemyAI enemyAI = new RandomEnemyAI();

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

        for (BookingGamePlayer player : this.players) {

            System.out.println(player);
            player.sendUpdate("board", player, state.getBoard().toString());
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
        this.updateScore(nextState);



        for (Enemy enemy : nextState.getEnemies()) {
            nextState.getBoard().updateComplete(players, nextState);
            enemy = this.enemyAI.transform(enemy, nextState);
        }

        if (roundNumber % BookingGameEngine.configuration.get("snippet_spawn_rate") == 0) {
            for (int i = 0; i < BookingGameEngine.configuration.get("snippet_spawn_count"); i++)
                newBoard.addRandomSnippet();
        }

        if (roundNumber > BookingGameEngine.configuration.get("enemy_spawn_delay")) {
            if (roundNumber % (BookingGameEngine.configuration.get("enemy_spawn_rate") - BookingGameEngine.configuration.get("enemy_spawn_delay")) == 0) {
                for (int i = 0; i < BookingGameEngine.configuration.get("enemy_spawn_count"); i++)
                    newBoard.addRandomSnippet();
            }
        }

        nextState.setRepresentationString(players);
        newBoard.dump(this.players, nextState);
        return nextState;
    }

    @Override
    public boolean hasGameEnded(BookingGameState state) {
        return (this.gameOver || this.roundNumber >= BookingGameEngine.configuration.get("max_rounds"));
    }

    @Override
    public BookingGamePlayer getWinner() {
        return null;
    }

    @Override
    public double getScore() {
        return 0;
    }


    private void updateScore(BookingGameState state) {
        BookingGameMove move = state.getMoves().get(0);
        BookingGamePlayer player = move.getPlayer();
    }

    public void setEnemyAI(EnemyAI enemyAI) {
        this.enemyAI = enemyAI;
    }
}
