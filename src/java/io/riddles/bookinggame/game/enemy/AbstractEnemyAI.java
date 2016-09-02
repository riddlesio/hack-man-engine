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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.move.MoveType;

/**
 * io.riddles.bookinggame.game.enemy.AbstractEnemy - Created on 29-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
abstract class AbstractEnemyAI implements EnemyAIInterface {

    ArrayList<MoveType> getAvailableDirections(BookingGameEnemy enemy, BookingGameBoard board) {
        if (enemy.getDirection() == null) {
            return (ArrayList<MoveType>) MoveType.getMovingMoveTypes();
        }

        return MoveType.getMovingMoveTypes().stream()
                .filter(moveType ->
                        !moveType.equals(enemy.getDirection().getOppositeMoveType()) &&
                            board.isEmptyInDirection(enemy.getCoordinate(), moveType))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Makes movements that are mandatory
    Point mandatoryTranform(BookingGameEnemy enemy,
                            BookingGameBoard board, ArrayList<MoveType> availableDirections) {
        Point coordinate = enemy.getCoordinate();
        Point outOfSpawn = moveOutOfSpawn(enemy, board);

        if (outOfSpawn != null) return outOfSpawn;

        switch (availableDirections.size()) {
            // No directions available, turn around
            case 0:
                MoveType oppositeDirection = enemy.getDirection().getOppositeMoveType();
                return board.getCoordinateAfterMove(coordinate, oppositeDirection);
            // Only one direction available, go that way
            case 1:
                return board.getCoordinateAfterMove(coordinate, availableDirections.get(0));
        }

        return null;
    }

    // Forces the enemy to move out of the spawn area
    private Point moveOutOfSpawn(BookingGameEnemy enemy, BookingGameBoard board) {
        Point coordinate = enemy.getCoordinate();

        if (board.isOnSpawnPoint(coordinate)) {
            ArrayList<Point> validNeighbors = board.getValidNeighbors(coordinate);

            if (validNeighbors.size() == 1) {
                return validNeighbors.get(0);
            }

            if (validNeighbors.size() > 1) {
                for (Point neighbor : validNeighbors) {
                    if (!board.isOnSpawnPoint(neighbor)) {
                        return neighbor;
                    }
                }
            }
        } else if (board.isOnSpawnGate(coordinate)) {
            ArrayList<Point> validNeighbors = board.getValidNeighbors(coordinate);

            for (Point neighbor : validNeighbors) {
                if (!board.isOnSpawnPoint(neighbor) && !board.isOnSpawnGate(neighbor)) {
                    return neighbor;
                }
            }
        }

        return null;
    }
}
