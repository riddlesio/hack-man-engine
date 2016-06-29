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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.bookinggame.BookingGame;
import io.riddles.bookinggame.game.move.ActionType;
import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.bookinggame.game.move.BookingGameMoveDeserializer;
import io.riddles.bookinggame.game.data.Record;
import io.riddles.javainterface.game.processor.AbstractProcessor;

/**
 * io.riddles.catchfrauds.interface.BookingGameProcessor - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGameProcessor extends AbstractProcessor<BookingGamePlayer, BookingGameState> {

    private ArrayList<String> checkPointValues;
    private ArrayList<Record> records;
    private int roundNumber;
    private boolean gameOver;
    private double scoreDelta; // subtracted from player score for each mistake

    public BookingGameProcessor(ArrayList<BookingGamePlayer> players, ArrayList<Record> records) {
        super(players);
        this.records = records;
        this.checkPointValues = new ArrayList<>();
        this.gameOver = false;
        this.scoreDelta = 100.0 / records.size();
    }

    @Override
    public void preGamePhase() {
        for (BookingGamePlayer player : this.players) {
            storeCheckpointInput(player.requestMove(ActionType.CHECKPOINTS.toString()));
        }
    }

    @Override
    public BookingGameState playRound(int roundNumber, BookingGameState state) {
        this.roundNumber = roundNumber;
        LOGGER.info(String.format("Playing round %d", roundNumber));

        int checkPointCount = checkPointValues.size();
        BookingGameState nextState = null;

        for (BookingGamePlayer player : this.players) {

            Record record = this.records.get(this.roundNumber - 1);

            // send next record and ask bot's assessment of the record
            player.sendUpdate("next_record", player, record.toBotString());
            String response = player.requestMove(ActionType.RECORD.toString());

            // parse the response
            BookingGameMoveDeserializer deserializer = new BookingGameMoveDeserializer(
                    player, checkPointCount);
            BookingGameMove move = deserializer.traverse(response);

            // create the next state
            nextState = new BookingGameState(state, move, roundNumber, record.isFraudulent());

            this.updateScore(nextState);

            // stop game if bot returns nothing
            if (response == null) {
                this.gameOver = true;
            }
        }

        return nextState;
    }

    @Override
    public boolean hasGameEnded(BookingGameState state) {
        return this.gameOver || this.roundNumber >= this.records.size();
    }

    @Override
    public BookingGamePlayer getWinner() {
        return null;
    }

    @Override
    public double getScore() {
        double score = this.getPlayers().get(0).getScore();
        BigDecimal bdScore = new BigDecimal(score);

        return bdScore.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public ArrayList<String> getCheckPointValues() {
        return this.checkPointValues;
    }

    private void updateScore(BookingGameState state) {
        BookingGameMove move = state.getMoves().get(0);
        BookingGamePlayer player = move.getPlayer();

        player.updateScore(state.isFraudulent(), move.isRefused(), this.scoreDelta);
    }

    private void storeCheckpointInput(String input) {
        if (input.length() <= 0) return;

        String[] values = input.split(";");
        for (String value : values) {
            value = value.trim();
            if (value.length() > 0) {
                this.checkPointValues.add(value);
            }
        }
    }
}