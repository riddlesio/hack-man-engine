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

package io.riddles.bookinggame.game.player;

import io.riddles.javainterface.game.player.AbstractPlayer;
import io.riddles.bookinggame.game.data.Coordinate;

/**
 * io.riddles.catchfrauds.game.player.BookingGameMovePlayer - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGamePlayer extends AbstractPlayer {

    private int snippets, weapons;
    private Coordinate c;
    private int paralysis;

    public BookingGamePlayer(int id) {
        super(id);
        this.snippets = 0;
        this.weapons = 1;
        this.paralysis = 0;
        this.c = new Coordinate (0,0);
    }

    public void updateSnippets(int delta) {
        this.snippets+=delta;
    }
    public void updateWeapons(int delta) {
        this.weapons+=delta;
    }

    public int getSnippets() {
        return this.snippets;
    }
    public int getWeapons() { return this.weapons; }
    public Coordinate getCoordinate() { return this.c; }

    public void setCoordinate(Coordinate c) {
        this.c = c;
    }

    public String toString() {
        return "Player " + this.getId() + " coord " + this.getCoordinate() + " snippets " + this.snippets + " weapons " + weapons + " paralysis " + paralysis;
    }

    public void updateParalysis() {
        if (this.paralysis > 0) {
            this.paralysis--;
        }
    }

    public Boolean isParalyzed() {
        return (this.paralysis > 0);
    }

    public void paralyse(int p) {
        this.paralysis+=p;
    }

}
