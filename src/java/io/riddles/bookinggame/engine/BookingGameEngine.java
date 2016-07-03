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

    @Override
    protected void parseSetupInput(String input) {
        String[] split = input.split(" ");
        String command = split[0];
        if (command.equals("bot_ids")) {
            String[] ids = split[1].split(",");
            for (int i = 0; i < ids.length; i++) {
                BookingGamePlayer player = createPlayer(Integer.parseInt(ids[i]));

                if (this.botInputFiles != null)
                    player.setInputFile(this.botInputFiles[i]);

                this.players.add(player);
            }
        } else if (command.equals("initial_snippet_count")) {
            configuration.put("initial_snippet_count", Integer.parseInt(split[1]));
        } else if (command.equals("snippet_spawn_rate")) {
            configuration.put("snippet_spawn_rate", Integer.parseInt(split[1]));
        } else if (command.equals("snippet_spawn_count")) {
            configuration.put("snippet_spawn_count", Integer.parseInt(split[1]));
        } else if (command.equals("initial_bug_count")) {
            configuration.put("initial_bug_count", Integer.parseInt(split[1]));
        } else if (command.equals("bug_spawn_delay")) {
            configuration.put("bug_spawn_delay", Integer.parseInt(split[1]));
        } else if (command.equals("bug_spawn_rate")) {
            configuration.put("bug_spawn_rate", Integer.parseInt(split[1]));
        } else if (command.equals("bug_spawn_count")) {
            configuration.put("bug_spawn_count", Integer.parseInt(split[1]));
        } else if (command.equals("max_rounds")) {
            configuration.put("max_rounds", Integer.parseInt(split[1]));
        } else if (command.equals("bug_snippet_loss")) {
            configuration.put("bug_snippet_loss", Integer.parseInt(split[1]));
        } else if (command.equals("weapon_snippet_loss")) {
            configuration.put("weapon_snippet_loss", Integer.parseInt(split[1]));
        } else if (command.equals("weapon_paralysis_duration")) {
            configuration.put("weapon_paralysis_duration", Integer.parseInt(split[1]));
        }
    }
}
