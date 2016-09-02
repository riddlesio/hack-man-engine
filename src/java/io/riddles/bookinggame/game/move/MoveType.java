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

package io.riddles.bookinggame.game.move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.riddles.bookinggame.engine.BookingGameEngine;

/**
 * io.riddles.bookinggame.game.move.MoveType
 *
 * All move types
 *
 * @author Jim van Eeden <jim@riddles.io>
 */
public enum MoveType {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    PASS,
    PARALYZED;

    public static List<MoveType> getMovingMoveTypes() {
        ArrayList<MoveType> movingMoveTypes = new ArrayList<>();

        movingMoveTypes.add(UP);
        movingMoveTypes.add(DOWN);
        movingMoveTypes.add(LEFT);
        movingMoveTypes.add(RIGHT);

        return movingMoveTypes;
    }

    public static MoveType getRandomMovingMoveType() {
        ArrayList<MoveType> movingMoveTypes = (ArrayList<MoveType>) getMovingMoveTypes();

        return movingMoveTypes.get(BookingGameEngine.RANDOM.nextInt(movingMoveTypes.size()));
    }

    public MoveType getOppositeMoveType() {
        switch(this) {
            case LEFT:
                return MoveType.RIGHT;
            case RIGHT:
                return MoveType.LEFT;
            case UP:
                return MoveType.DOWN;
            case DOWN:
                return MoveType.UP;
            default:
                return this;
        }
    }

    public String toString() {
        return this.name().toLowerCase();
    }
}