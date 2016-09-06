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
import java.util.HashMap;
import java.util.stream.Collectors;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.enemy.EnemyAIInterface;
import io.riddles.bookinggame.game.enemy.BookingGameEnemy;
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
    private EnemyAIInterface enemyAI;
    private BookingGamePlayer winner;
    private int score;

    public BookingGameProcessor(ArrayList<BookingGamePlayer> players, EnemyAIInterface enemyAI) {
        super(players);
        this.gameOver = false;
        this.enemyAI = enemyAI;
    }

    @Override
    public void preGamePhase() {}

    @Override
    public BookingGameState playRound(int roundNumber, BookingGameState state) {
        LOGGER.info(String.format("Playing round %d", roundNumber));
        this.roundNumber = roundNumber;

        Configuration config = BookingGameEngine.configuration;

        BookingGameState nextState = state.createNextState(roundNumber);
        BookingGameBoard nextBoard = nextState.getBoard();

        // Spawn snippets
        if (roundNumber % config.getInt("snippetSpawnRate") == 0) {
            for (int i = 0; i < config.getInt("snippetSpawnCount"); i++) {
                nextBoard.addRandomSnippet();
            }
        }
        // Spawn weapons
        if (roundNumber >= config.getInt("weaponSpawnDelay") &&
                roundNumber % config.getInt("weaponSpawnRate") == 0) {
            for (int i = 0; i < config.getInt("weaponSpawnCount"); i++) {
                nextBoard.addRandomWeapon();
            }
        }

        // Send updates and Update player movements
        for (BookingGamePlayer player : this.players) {
            sendUpdates(player, nextState);

            String response = player.requestMove(ActionType.MOVE.toString());

            BookingGamePlayer nextPlayer = nextBoard.getPlayerById(player.getId());

            // parse the response
            BookingGameMoveDeserializer deserializer = new BookingGameMoveDeserializer(nextPlayer);
            BookingGameMove move = deserializer.traverse(response);

            nextState.getMoves().add(move);

            // move player to new coordinate
            if (!move.isInvalid()) {
                Point newCoordinate = nextBoard.getCoordinateAfterMove(
                        nextPlayer.getCoordinate(), move.getMoveType());
                if (nextBoard.isCoordinateValid(newCoordinate, true)) {
                    nextPlayer.setCoordinate(newCoordinate);
                }
            }

            nextPlayer.updateParalysis();
        }

        // Move enemies
        for (BookingGameEnemy enemy : nextState.getBoard().getEnemies()) {
            enemy.performMovement(nextState);
        }

        // Spawn new enemies
        int snippetsEaten = nextState.getSnippetsEaten();
        if (snippetsEaten >= config.getInt("enemySpawnDelay") &&
                snippetsEaten % config.getInt("enemySpawnRate") == 0) {
            for (int i = 0; i < config.getInt("enemySpawnCount"); i++) {
                nextBoard.spawnEnemy(this.enemyAI);
            }
        }

        // Calculate changes due to collisions
        pickUpSnippets(nextState);
        pickUpWeapons(nextState);
        collideWithEnemies(nextState);
        collideWithPlayers(nextState);

        // Update winner and scores
        updateWinner(nextState);
        updateScore(nextState);

        return nextState;
    }

    private void sendUpdates(BookingGamePlayer player, BookingGameState state) {
        player.sendUpdate("round", state.getRoundNumber());
        player.sendUpdate("field", state.getBoard().toString());

        for (BookingGamePlayer target : state.getBoard().getPlayers()) {
            player.sendUpdate("snippets", target, target.getSnippets());
            player.sendUpdate("has_weapon", target, target.hasWeapon() + "");
            player.sendUpdate("is_paralyzed", target, target.isParalyzed() + "");
        }
    }

    private void pickUpSnippets(BookingGameState state) {
        BookingGameBoard board = state.getBoard();
        ArrayList<BookingGamePlayer> playersOnSnippet = getPlayersOnItem(board, "C");

        for (BookingGamePlayer player : playersOnSnippet) {
            player.updateSnippets(1);
            state.setSnippetsEaten(1);
            board.setFieldAt(player.getCoordinate(), ".");
        }
    }

    private void pickUpWeapons(BookingGameState state) {
        BookingGameBoard board = state.getBoard();
        ArrayList<BookingGamePlayer> playersOnWeapon = getPlayersOnItem(board, "W");

        for (BookingGamePlayer player : playersOnWeapon) {
            player.setWeapon(true);
            board.setFieldAt(player.getCoordinate(), ".");
        }
    }

    private ArrayList<BookingGamePlayer> getPlayersOnItem(BookingGameBoard board, String type) {
        ArrayList<BookingGamePlayer> playersOnItems = board.getPlayers().stream()
                .filter(player -> board.getFieldAt(player.getCoordinate()).equals(type))
                .collect(Collectors.toCollection(ArrayList::new));

        if (playersOnItems.size() <= 0) {
            return playersOnItems;
        }

        // Get all players by coord
        HashMap<String, ArrayList<BookingGamePlayer>> playersByCoord = new HashMap<>();
        for (BookingGamePlayer player : playersOnItems) {
            String coordString = player.getCoordinate().toString();
            ArrayList<BookingGamePlayer> coordPlayers = playersByCoord.get(coordString);

            if (coordPlayers == null) {
                coordPlayers = new ArrayList<>();
            }

            coordPlayers.add(player);
            playersByCoord.put(coordString, coordPlayers);
        }

        // Remove a random player if on the same item
        ArrayList<BookingGamePlayer> playersNotOnSameItem = new ArrayList<>();
        for (ArrayList<BookingGamePlayer> playersOnSameItem : playersByCoord.values()) {
            int randomIndex = BookingGameEngine.RANDOM.nextInt(playersOnSameItem.size());
            BookingGamePlayer player = playersOnSameItem.get(randomIndex);
            playersNotOnSameItem.add(player);
        }

        return playersNotOnSameItem;
    }

    private void collideWithEnemies(BookingGameState state) {
        BookingGameBoard board = state.getBoard();
        Configuration config = BookingGameEngine.configuration;

        ArrayList<BookingGameEnemy> killedEnemies = new ArrayList<>();

        for (BookingGamePlayer player : board.getPlayers()) {
            for (BookingGameEnemy enemy : board.getEnemies()) {
                if (enemy.getCoordinate().equals(player.getCoordinate())) {
                    if (player.hasWeapon()) { // player kills enemy
                        player.setWeapon(false);
                    } else { // player loses snippets
                        player.updateSnippets(-config.getInt("enemySnippetLoss"));
                    }
                    killedEnemies.add(enemy);
                }
            }
        }

        killedEnemies.forEach(board::killEnemy);
    }

    private void collideWithPlayers(BookingGameState state) {
        BookingGameBoard board = state.getBoard();
        Configuration config = BookingGameEngine.configuration;

        for (BookingGamePlayer player : board.getPlayers()) {
            if (!player.hasWeapon()) continue;

            for (BookingGamePlayer other : board.getPlayers()) {
                if (player != other && player.getCoordinate().equals(other.getCoordinate())) {
                    player.setWeapon(false);

                    int snippetLoss = config.getInt("weaponSnippetLoss");

                    other.paralyse(config.getInt("weaponParalysisDuration"));
                    other.updateSnippets(-snippetLoss);

                    // lost snippets are spread accross the map
                    for (int n = 0; n < snippetLoss; n++) {
                        board.addRandomSnippet();
                    }
                }
            }
        }
    }

    private void updateWinner(BookingGameState state) {
        ArrayList<BookingGamePlayer> players = state.getBoard().getPlayers();

        ArrayList<BookingGamePlayer> playersWithSnippets = players.stream()
                .filter(player -> !player.hasCollectedSnippet() || player.getSnippets() > 0)
                .collect(Collectors.toCollection(ArrayList::new));

        if (playersWithSnippets.size() <= 0) { /* All players lost their snippets, it's a draw */
            this.gameOver = true;
            return;
        } else if (playersWithSnippets.size() == 1) { /* Only one player left with snippets, there's a winner */
            this.winner = playersWithSnippets.get(0);
            return;
        }

        /* Player with most snippets wins */
        if (this.roundNumber >= BookingGameEngine.configuration.getInt("maxRounds")) {
            ArrayList<BookingGamePlayer> winners = new ArrayList<>();

            int max = 0;
            for (BookingGamePlayer player : players) {
                if (player.getSnippets() > max) {
                    max = player.getSnippets();
                    winners.clear();
                    winners.add(player);
                } else if (player.getSnippets() == max) {
                    winners.add(player);
                }
            }

            if (winners.size() == 1) {
                this.winner = winners.get(0);
            } else {
                this.gameOver = true;
                // multiple winners?
            }
        }
    }

    private void updateScore(BookingGameState state) {
        int max = 0;

        for (BookingGamePlayer player : state.getBoard().getPlayers()) {
            if (player.getSnippets() > max) {
                max = player.getSnippets();
            }
        }

        this.score = max;
    }

    @Override
    public boolean hasGameEnded(BookingGameState state) {
        return getWinner() != null || this.gameOver;
    }

    @Override
    public BookingGamePlayer getWinner() {
        return this.winner;
    }

    @Override
    public double getScore() {
        return this.score;
    }

    public void setEnemyAI(EnemyAIInterface enemyAI) {
        this.enemyAI = enemyAI;
    }
}
