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

package io.riddles.bookinggame.game.enemy;

import io.riddles.bookinggame.game.move.MoveType;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.awt.*;

/**
 * io.riddles.bookinggame.game.enemy.RandomEnemyAI - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class RandomEnemyAI extends AbstractEnemyAI {

    public Point transform(Enemy enemy, BookingGameState state) {

        /* Go around corners */
        MoveType randomDirection = MoveType.getRandomMoveType();
        MoveType oldDirection = enemy.getDirection();
        while (randomDirection == enemy.getDirection().getOppositeMoveType()) {
            randomDirection = MoveType.getRandomMoveType();
        }
        enemy.setDirection(randomDirection);

        /* Bump into wall */
        Point newCoordinate = getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection());
        if (!state.getBoard().isEmpty(newCoordinate)) {
            int wallCounter = 0;
            if (!isEmptyInDirection(enemy.getCoordinate(), MoveType.DOWN, state.getBoard())) {
                wallCounter++;
            }
            if (!isEmptyInDirection(enemy.getCoordinate(), MoveType.UP, state.getBoard())) {
                wallCounter++;
            }
            if (!isEmptyInDirection(enemy.getCoordinate(), MoveType.LEFT, state.getBoard())) {
                wallCounter++;
            }
            if (!isEmptyInDirection(enemy.getCoordinate(), MoveType.RIGHT, state.getBoard())) {
                wallCounter++;
            }
            if (wallCounter > 2) { /* Dead end, go opposite direction */
                randomDirection = oldDirection.getOppositeMoveType();
                enemy.setDirection(randomDirection);
                newCoordinate = getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection());
            } else {
                /* Field blocked, find a new direction */
                while (!isEmptyInDirection(enemy.getCoordinate(), enemy.getDirection(), state.getBoard())) {
                    randomDirection = MoveType.getRandomMoveType();
                    while (randomDirection == oldDirection.getOppositeMoveType()) {
                        randomDirection = MoveType.getRandomMoveType();
                    }
                    enemy.setDirection(randomDirection);
                    if (state.getBoard().isEmpty(getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection()))) {
                        newCoordinate = getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection());
                    }
                }
            }
        }

        return newCoordinate;
    }
}
