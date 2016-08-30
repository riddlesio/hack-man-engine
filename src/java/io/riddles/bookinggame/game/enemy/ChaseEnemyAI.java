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

import io.riddles.bookinggame.game.board.BookingGameBoard;
import io.riddles.bookinggame.game.move.MoveType;
import io.riddles.bookinggame.game.move.Map;
import io.riddles.bookinggame.game.move.Node;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 * io.riddles.bookinggame.game.enemy.ChaseEnemyAI - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class ChaseEnemyAI extends AbstractEnemyAI {

    public Point transform(Enemy enemy, BookingGameState state) {
        MoveType oldDirection = enemy.getDirection();

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

        MoveType newDirection = getChaseDirection(enemy.getCoordinate(),
                enemy.getPreviousCoordinate(), state.getBoard());

        if (isEmptyInDirection(enemy.getCoordinate(), newDirection, state.getBoard())) {
            if (newDirection != oldDirection.getOppositeMoveType() || wallCounter == 3) {
                enemy.setDirection(newDirection);
            } else {
                System.out.println(" Can't go " + newDirection);
            }
        }

        Point newCoordinate = getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection());
        if (state.getBoard().isCoordinateValid(newCoordinate)) {
            return newCoordinate;
        }

        return enemy.getCoordinate();
    }

    private MoveType getChaseDirection(Point coordinate, Point prevCoordinate, BookingGameBoard b) {
        Map map = new Map(b.getWidth(), b.getHeight());
        map.initFromBoard(b, prevCoordinate);
        Point newCoordinate = getNearestPlayerCoordinate(map, coordinate, b);

        if (!coordinate.equals(newCoordinate)) {
            LinkedList<Node> path = (LinkedList<Node>) map.findPath(coordinate, newCoordinate);
            if (path.size() > 0) {
                return getDirection(coordinate,
                        new Point(path.get(0).getXPosition(), path.get(0).getYPosition()));
            }
        }

        return MoveType.getRandomMoveType();
    }

    private Point getNearestPlayerCoordinate(Map map, Point coordinate, BookingGameBoard board) {
        int nearestX = 0, nearestY = 0;
        int smallestDistance = Integer.MAX_VALUE;
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (isInteger(board.getField(new Point(x,y)))) {
                    Point newCoordinate = new Point(x, y);

                    if (!coordinate.equals(newCoordinate)) {
                        List<Node> l = map.findPath(coordinate, newCoordinate);
                        if (l.size() < smallestDistance && l.size() > 1 ) {
                            nearestX = x; nearestY = y; smallestDistance = l.size();
                        }
                    }
                }
            }
        }
        return new Point(nearestX, nearestY);
    }

    private boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
