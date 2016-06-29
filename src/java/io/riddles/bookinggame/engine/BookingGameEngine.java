package io.riddles.bookinggame.engine;

import io.riddles.bookinggame.game.processor.BookingGameProcessor;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.javainterface.engine.AbstractEngine;

/**
 * Created by joost on 6/27/16.
 */
public class BookingGameEngine extends AbstractEngine<BookingGameProcessor, BookingGamePlayer, BookingGameState> {
    @Override
    protected BookingGameState getInitialState() {
        return null;
    }

    @Override
    protected BookingGamePlayer createPlayer(int id) {
        return null;
    }

    @Override
    protected BookingGameProcessor createProcessor() {
        return null;
    }

    @Override
    protected void sendGameSettings(BookingGamePlayer player) {

    }

    @Override
    protected String getPlayedGame(BookingGameState initialState) {
        return null;
    }
}
