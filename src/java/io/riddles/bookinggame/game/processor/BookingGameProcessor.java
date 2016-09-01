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

import java.awt.*;
import java.util.ArrayList;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.enemy.ChaseWithChanceEnemyAI;
import io.riddles.bookinggame.game.enemy.EnemyAIInterface;
import io.riddles.bookinggame.game.enemy.Enemy;
import io.riddles.bookinggame.game.move.*;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.game.processor.AbstractProcessor;

/**
 * io.riddles.bookinggame.game.processor.BookingGameProcessor - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class BookingGameProcessor extends AbstractProcessor<BookingGamePlayer, BookingGameState> {

    private int roundNumber;
    private boolean gameOver;

    private EnemyAIInterface enemyAI = (EnemyAIInterface) new ChaseWithChanceEnemyAI(
            0.2, (0.8 / BookingGameEngine.configuration.getInt("maxRounds")));

    public BookingGameProcessor(ArrayList<BookingGamePlayer> players) {
        super(players);
        this.gameOver = false;
    }

    @Override
    public void preGamePhase() {}

    @Override
    public BookingGameState playRound(int roundNumber, BookingGameState state) {
        LOGGER.info(String.format("Playing round %d", roundNumber));
        this.roundNumber = roundNumber;

        BookingGameLogic logic = new BookingGameLogic();
        BookingGameState nextState = state.createNextState(roundNumber);
        BookingGameBoard nextBoard = nextState.getBoard();

        // Update player movements

        for (BookingGamePlayer player : this.players) {
            player.sendUpdate("field", player, state.getBoard().toString());
            String response = player.requestMove(ActionType.MOVE.toString());

            BookingGamePlayer nextPlayer = nextBoard.getPlayerById(player.getId());

            // parse the response
            BookingGameMoveDeserializer deserializer = new BookingGameMoveDeserializer(nextPlayer);
            BookingGameMove move = deserializer.traverse(response);

            nextState.getMoves().add(move);

            if (!move.isInvalid()) {
                logic.transformBoard(nextState, move, this.players);
            }

            nextPlayer.updateParalysis();
        }

        nextBoard.updatePlayerMovements();

        // Update everything outside of player actions

        Configuration config = BookingGameEngine.configuration;

        // Move enemies
        for (Enemy enemy : nextState.getBoard().getEnemies()) {
            enemy.performMovement(nextState);
        }

        // Spawn new enemies
        int snippetsEaten = nextState.getSnippetsEaten();
        int oldSnippetsEaten = state.getSnippetsEaten();
        if (oldSnippetsEaten != snippetsEaten) { /* A snippet has been eaten */
            if (snippetsEaten > config.getInt("enemySpawnDelay")) {
                if ((snippetsEaten - config.getInt("enemySpawnDelay")) % config.getInt("enemySpawnRate") == 0) {
                    for (int i = 0; i < config.getInt("enemySpawnCount"); i++) {
                        MoveType randomDirection = MoveType.getRandomMoveType();
                        Point startField = nextBoard.getEnemyStartField();
                        Enemy enemy = new Enemy(startField, randomDirection, this.enemyAI);
                        nextState.getBoard().addEnemy(enemy);
                    }
                }
            }
        }

        // Spawn snippets
        if (roundNumber % config.getInt("snippetSpawnRate") == 0) {
            for (int i = 0; i < config.getInt("snippetSpawnCount"); i++) {
                nextBoard.addRandomSnippet();
            }
        }

        nextBoard.updateEnemyMovements();
        return nextState;
    }

    @Override
    public boolean hasGameEnded(BookingGameState state) {
        return getWinner() != null || this.gameOver ||
                this.roundNumber >= BookingGameEngine.configuration.getInt("maxRounds");
    }

    @Override
    public BookingGamePlayer getWinner() {
        int playersWithSnippets = 0;

        /* This is double work */
        for (BookingGamePlayer player : this.players) {
            if (!player.hasCollectedSnippet() || player.getSnippets() > 0) {
                playersWithSnippets++;
            }
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

        if (this.roundNumber >= BookingGameEngine.configuration.getInt("maxRounds")) {
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
        int max = 0;
        for (BookingGamePlayer player : this.players) {
            if (player.getSnippets() > max) {
                max = player.getSnippets();
            }
        }

        return max;
    }

    public void setEnemyAI(EnemyAIInterface enemyAI) {
        this.enemyAI = enemyAI;
    }
}
