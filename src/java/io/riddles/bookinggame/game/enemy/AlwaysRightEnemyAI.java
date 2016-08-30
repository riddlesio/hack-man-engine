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

import io.riddles.bookinggame.game.move.MoveType;
import io.riddles.bookinggame.game.state.BookingGameState;

/**
 * io.riddles.bookinggame.game.enemy.AlwayRightEnemyAI - Created on 7/18/16.
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class AlwaysRightEnemyAI extends AbstractEnemyAI {

    public Point transform(Enemy enemy, BookingGameState state) {
        MoveType d = MoveType.RIGHT;
        enemy.setDirection(d);

        Point newCoordinate = getMovedCoordinate(enemy.getCoordinate(), enemy.getDirection());
        if (state.getBoard().isEmpty(newCoordinate)) {
            return newCoordinate;
        }

        return enemy.getCoordinate();
    }
}
