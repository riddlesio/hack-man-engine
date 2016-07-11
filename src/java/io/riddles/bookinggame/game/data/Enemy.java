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

package io.riddles.bookinggame.game.data;

import io.riddles.bookinggame.game.data.Board;
import io.riddles.bookinggame.game.data.Coordinate;
import io.riddles.bookinggame.game.data.MoveType;

import java.awt.event.MouseEvent;

/**
 * io.riddles.catchfrauds.game.move.Enemy - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public class Enemy {


    private Coordinate coordinate;
    private MoveType direction;

    public Enemy(Coordinate c, MoveType d) {
        this.coordinate = c;
        this.direction = d;
    }

    public Coordinate getCoordinate() { return this.coordinate; }
    public void setCoordinate(Coordinate c) { this.coordinate = c; }

    public Coordinate getMovedCoordinate(MoveType m, Board b) {
        switch(m) {
            case UP:
                return new Coordinate(this.coordinate.getX(), this.coordinate.getY()-1);
            case DOWN:
                return new Coordinate(this.coordinate.getX(), this.coordinate.getY()+1);
            case RIGHT:
                return new Coordinate(this.coordinate.getX()+1, this.coordinate.getY());
            case LEFT:
                return new Coordinate(this.coordinate.getX()-1, this.coordinate.getY());
        }
        return this.coordinate;
    }

    public void update(Board b) {
        /* Use AI to make a move */
        /* TODO: Implement Chase, Scatter and Frightened modes? */

        Coordinate newC = getMovedCoordinate(this.direction, b);
        if (b.isEmpty(newC)) {
            /* Move there */
            this.coordinate = newC;
        } else {
            /* Field blocked, find a new direction */
        }


    }

    public MoveType getDirection() { return this.direction; }

    public String toString() {
        return "Enemy" + this.coordinate;
    }
}
