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

    Point getMovedCoordinate(Point coordinate, MoveType moveType) {
        if (moveType != null) {
            switch(moveType) {
                case UP:
                    return new Point(coordinate.x, coordinate.y - 1);
                case DOWN:
                    return new Point(coordinate.x, coordinate.y + 1);
                case RIGHT:
                    return new Point(coordinate.x + 1, coordinate.y);
                case LEFT:
                    return new Point(coordinate.x - 1, coordinate.y);
            }
        }
        return coordinate;
    }

    Boolean isEmptyInDirection(Point coordinate, MoveType moveType, BookingGameBoard board) {
        if (moveType == null) {
            return false;
        }

        Point newCoordinate = getMovedCoordinate(coordinate, moveType);
        return board.isCoordinateValid(newCoordinate);
    }

    MoveType getDirection(Point oldCoordinate, Point newCoordinate) {
        if (newCoordinate.x > oldCoordinate.x) return MoveType.RIGHT;
        if (newCoordinate.x < oldCoordinate.x) return MoveType.LEFT;
        if (newCoordinate.y > oldCoordinate.y) return MoveType.DOWN;
        if (newCoordinate.y < oldCoordinate.y) return MoveType.UP;
        return null;
    }
}
