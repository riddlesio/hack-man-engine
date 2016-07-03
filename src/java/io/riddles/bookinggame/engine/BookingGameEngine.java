package io.riddles.bookinggame.engine;

import io.riddles.bookinggame.game.data.Board;
import io.riddles.bookinggame.game.processor.BookingGameProcessor;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.bookinggame.game.BookingGameSerializer;

/**
 * Created by joost on 6/27/16.
 */
public class BookingGameEngine extends AbstractEngine<BookingGameProcessor, BookingGamePlayer, BookingGameState> {

    public BookingGameEngine() {
        super();
    }

    public BookingGameEngine(String wrapperFile, String[] botFiles) {
        super(wrapperFile, botFiles);
    }


    @Override
    protected BookingGamePlayer createPlayer(int id) {
        return new BookingGamePlayer(id);
    }

    @Override
    protected BookingGameProcessor createProcessor() {
        return new BookingGameProcessor(this.players);
    }

    @Override
    protected void sendGameSettings(BookingGamePlayer player) {
        //player.sendSetting("board", getInitialState().getBoard().toString());
    }

    @Override
    protected String getPlayedGame(BookingGameState initialState) {
        BookingGameSerializer serializer = new BookingGameSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    @Override
    protected BookingGameState getInitialState() {
        BookingGameState s = new BookingGameState();
        Board b = new Board(20, 11);
        b.initialiseFromString(getStandardBoard(), 20, 11);
        s.setBoard(b);
        return s;
    }


    private String getStandardBoard() {
        return  "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                "x,.,x,.,x,x,.,x,x,.,.,x,x,.,.,.,.,x,.,x," +
                "x,1,.,.,.,.,.,x,.,.,.,.,x,.,.,.,.,.,2,x," +
                "x,.,x,.,x,x,.,x,x,x,x,x,x,.,x,x,.,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }
}
