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

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;

import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.enemy.BookingGameEnemy;
import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.javainterface.game.state.AbstractStateSerializer;

/**
 * io.riddles.bookinggame.game.state.BookingGameStateSerializer - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class BookingGameStateSerializer extends AbstractStateSerializer<BookingGameState> {

    @Override
    public String traverseToString(BookingGameState state) {
        return visitState(state).toString();
    }

    @Override
    public JSONObject traverseToJson(BookingGameState state) throws NullPointerException {
        return visitState(state);
    }

    private JSONObject visitState(BookingGameState state) throws NullPointerException {
        JSONObject stateObj = new JSONObject();
        BookingGameBoard board = state.getBoard();

        JSONArray players = new JSONArray();
        JSONArray enemies = new JSONArray();
        JSONArray collectibles = new JSONArray();
        JSONArray weapons = new JSONArray();

        for (BookingGameMove move : state.getMoves()) {
            // players on the board are cloned, so we need those
            BookingGamePlayer player = move.getPlayer();

            JSONObject playerObj = new JSONObject();
            playerObj.put("id", player.getId());
            playerObj.put("x", player.getCoordinate().x);
            playerObj.put("y", player.getCoordinate().y);
            playerObj.put("score", player.getSnippets());
            playerObj.put("move", move.toString());
            playerObj.put("hasWeapon", player.hasWeapon());

            Exception exception = move.getException();
            if (exception != null) {
                playerObj.put("exception", move.getException().getMessage());
            } else {
                playerObj.put("exception", JSONObject.NULL);
            }

            players.put(playerObj);
        }

        for (BookingGameEnemy enemy : board.getEnemies()) {
            JSONObject enemyObj = new JSONObject();
            enemyObj.put("x", enemy.getCoordinate().x);
            enemyObj.put("y", enemy.getCoordinate().y);
            enemyObj.put("move", enemy.getDirection());
            enemyObj.put("id", enemy.getId());

            enemies.put(enemyObj);
        }

        for (Point collectibleCoordinate : board.getSnippetLocations()) {
            JSONObject collectible = new JSONObject();
            collectible.put("x", collectibleCoordinate.x);
            collectible.put("y", collectibleCoordinate.y);

            collectibles.put(collectible);
        }

        for (Point weaponCoordinate : board.getWeaponLocations()) {
            JSONObject weapon = new JSONObject();
            weapon.put("x", weaponCoordinate.x);
            weapon.put("y", weaponCoordinate.y);

            weapons.put(weapon);
        }

        stateObj.put("round", state.getRoundNumber());
        stateObj.put("players", players);
        stateObj.put("enemies", enemies);
        stateObj.put("collectibles", collectibles);
        stateObj.put("weapons", weapons);

        return stateObj;
    }
}
