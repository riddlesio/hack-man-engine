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

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.enemy.Enemy;
import io.riddles.bookinggame.game.player.BookingGamePlayer;

import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.javainterface.configuration.Configuration;

import java.awt.*;
import java.util.ArrayList;

/**
 * io.riddles.bookinggame.game.processor.BookingGameLogic - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class BookingGameLogic {

    public void transformBoard (
            BookingGameState state, BookingGameMove move, ArrayList<BookingGamePlayer> players)  {

        transformPlayerLocation(state, move, players);
    }

    private void transformPlayerLocation (
            BookingGameState state, BookingGameMove move, ArrayList<BookingGamePlayer> players) {
        BookingGamePlayer player = move.getPlayer();
        BookingGameBoard board = state.getBoard();

        Point coordinate = player.getCoordinate();
        Configuration config = BookingGameEngine.configuration;

        Point newCoordinate = newCoordinate = new Point(coordinate);
        switch(move.getMoveType()) {
            case UP:
                Point up = new Point(coordinate.x, coordinate.y - 1);
                if (board.isCoordinateValid(up)) {
                    newCoordinate = up;
                }
                break;
            case DOWN:
                Point down = new Point(coordinate.x, coordinate.y + 1);
                if (board.isCoordinateValid(down)) {
                    newCoordinate = down;
                }
                break;
            case RIGHT:
                Point right = new Point(coordinate.x + 1, coordinate.y);
                if (board.isCoordinateValid(right)) {
                    newCoordinate = right;
                }
                break;
            case LEFT:
                Point left = new Point(coordinate.x - 1, coordinate.y);
                if (board.isCoordinateValid(left)) {
                    newCoordinate = left;
                }
                break;
        }

        boolean otherPlayerPresent = false;
        for (BookingGamePlayer p : players) {
            if (player.getId() != p.getId()) {
                if (player.getCoordinate().equals(newCoordinate)) {
                    otherPlayerPresent = true;
                }
            }
        }
        if (otherPlayerPresent) { /* another player at newC */
            if (player.getWeapons() > 0) { /* Player has weapon */
                players.stream()
                    .filter(otherPlayer -> otherPlayer.getId() != player.getId())
                    .forEach(otherPlayer -> {
                        otherPlayer.paralyse(config.getInt("weaponParalysisDuration"));
                        otherPlayer.updateSnippets(-config.getInt("weaponSnippetLoss"));
                        state.updateSnippetsEaten(config.getInt("weaponSnippetLoss"));
                    });
            }

            newCoordinate = new Point(coordinate); /* Stay in position */
        }

        for (Enemy enemy : board.getEnemies()) {
            if (enemy.getCoordinate().equals(newCoordinate)) {
                /* Collision with enemy */
                if (player.getWeapons() > 0) { /* Player has weapon */
                    //System.out.println("PLAYER KILLS ENEMY");
                    player.updateWeapons(-1);
                    board.killEnemyAt(newCoordinate);
                } else { /* Player gets a hit */
                    //System.out.println("PLAYER HIT BY ENEMY");
                    int maxSnippets = config.getInt("enemySnippetLoss");
                    if (player.getSnippets() < maxSnippets) {
                        maxSnippets = player.getSnippets();
                    }

                    player.updateSnippets(-maxSnippets);

                    /* Spawn x snippets on map */
                    for (int i = 0; i < maxSnippets; i++) {
                        //System.out.println("Spawning snippet");
                        board.addSnippet(board.getLoneliestCoordinate());
                    }
                    newCoordinate = new Point(coordinate); /* Stay in position */

                    enemy.setCoordinate(enemy.getPreviousCoordinate());
                }
            }
        }

        switch (board.getFieldAt(newCoordinate)) {
            case "B": /* A bug */
                break;
            case "C": /* Code snippet */
                board.setFieldAt(newCoordinate, ".");
                player.updateSnippets(+1);
                state.updateSnippetsEaten(1);
                break;
            case "W": /* A Weapon */
                board.setFieldAt(newCoordinate, ".");
                player.updateWeapons(1);
                break;
        }

//        System.err.println(newCoordinate.toString());
        player.setCoordinate(newCoordinate);
        board.updateEnemyMovements();
    }
}
