package io.riddles.bookinggame.engine;

import io.riddles.bookinggame.game.data.BookingGameBoard;
import io.riddles.bookinggame.game.data.Coordinate;

import io.riddles.bookinggame.game.processor.BookingGameProcessor;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.bookinggame.game.BookingGameSerializer;

/**
 * Created by joost on 6/27/16.
 */
public class BookingGameEngine extends AbstractEngine<BookingGameProcessor, BookingGamePlayer, BookingGameState> {

    protected Coordinate[] startCoordinates;
    protected String bot_ids;
    public BookingGameEngine() {

        super();
        initialiseData();
    }

    public BookingGameEngine(String wrapperFile, String[] botFiles) {

        super(wrapperFile, botFiles);
        initialiseData();
    }

    protected void initialiseData() {
        this.startCoordinates = new Coordinate[4];
        /* TODO: Find start coordinates */
        this.startCoordinates[0] = new Coordinate(1, 5);
        this.startCoordinates[1] = new Coordinate(19, 5);
        this.startCoordinates[2] = new Coordinate(1, 7);
        this.startCoordinates[3] = new Coordinate(19, 7);
    }

    @Override
    protected BookingGamePlayer createPlayer(int id) {
        BookingGamePlayer p = new BookingGamePlayer(id);
        return p;
    }

    @Override
    protected BookingGameProcessor createProcessor() {
        for (int i = 0; i < this.players.size(); i++)
            this.players.get(i).updateSnippets(configuration.get("player_snippet_count"));
        return new BookingGameProcessor(this.players);
    }

    @Override
    protected void sendGameSettings(BookingGamePlayer player) {
        /* TODO: Send all settings needed */
        player.sendSetting("player_names", this.bot_ids);
        player.sendSetting("board", getInitialState().getBoard().toString());
        player.sendSetting("board_width", getInitialState().getBoard().getWidth());
        player.sendSetting("board_height", getInitialState().getBoard().getHeight());
        player.sendSetting("time_per_move", 5000); /* TODO: find this value */
        player.sendSetting("your_botid", player.getId());
    }

    @Override
    protected String getPlayedGame(BookingGameState initialState) {
        BookingGameSerializer serializer = new BookingGameSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    @Override
    protected BookingGameState getInitialState() {
        BookingGameState s = new BookingGameState();
        BookingGameBoard b = new BookingGameBoard(20, 11);
        String standardBoard = getStandardBoard();
        b.initialiseFromString(standardBoard, 20, 11);
        b.updateComplete(players, s);
        for (int i = 0; i < configuration.get("map_snippet_count"); i++) {
            b.addRandomSnippet();
        }
        s.setBoard(b);
        return s;
    }


    protected String getStandardBoard() {
        return  "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x," +
                "x,.,.,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,.,x,x,.,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,.,x,.,.,.,.,.,.,.,.,.,.,.,.,.,.,x,.,x," +
                "x,C,x,.,x,x,.,x,x,.,.,x,x,.,x,x,.,x,.,x," +
                "x,.,C,.,.,.,.,x,.,.,.,.,x,.,.,.,C,C,.,x," +
                "x,C,x,.,x,x,.,x,x,x,x,x,x,.,x,x,C,x,.,x," +
                "x,C,x,.,C,C,.,.,.,.,.,.,.,.,.,.,C,x,.,x," +
                "x,C,x,x,C,x,.,x,x,x,x,x,x,.,x,.,x,x,.,x," +
                "x,C,C,.,.,x,.,.,.,.,.,.,.,.,x,.,.,.,.,x," +
                "x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x";
    }

    @Override
    protected void parseSetupInput(String input) {
        String[] split = input.split(" ");
        String command = split[0];
        if (command.equals("bot_ids")) {
            this.bot_ids = split[1];
            String[] ids = split[1].split(",");
            for (int i = 0; i < ids.length; i++) {
                BookingGamePlayer player = createPlayer(Integer.parseInt(ids[i]));
                if (this.botInputFiles != null)
                    player.setInputFile(this.botInputFiles[i]);
                player.setCoordinate(getStartCoordinate(i));
                this.players.add(player);
            }
        } else if (command.equals("player_snippet_count")) {
            configuration.put("player_snippet_count", Integer.parseInt(split[1]));
        } else if (command.equals("map_snippet_count")) {
            configuration.put("map_snippet_count", Integer.parseInt(split[1]));
        } else if (command.equals("snippet_spawn_rate")) {
            configuration.put("snippet_spawn_rate", Integer.parseInt(split[1]));
        } else if (command.equals("snippet_spawn_count")) {
            configuration.put("snippet_spawn_count", Integer.parseInt(split[1]));
        } else if (command.equals("initial_enemy_count")) {
            configuration.put("initial_enemy_count", Integer.parseInt(split[1]));
        } else if (command.equals("enemy_spawn_delay")) {
            configuration.put("enemy_spawn_delay", Integer.parseInt(split[1]));
        } else if (command.equals("enemy_spawn_rate")) {
            configuration.put("enemy_spawn_rate", Integer.parseInt(split[1]));
        } else if (command.equals("enemy_spawn_count")) {
            configuration.put("enemy_spawn_count", Integer.parseInt(split[1]));
        } else if (command.equals("max_rounds")) {
            configuration.put("max_rounds", Integer.parseInt(split[1]));
        } else if (command.equals("enemy_snippet_loss")) {
            configuration.put("enemy_snippet_loss", Integer.parseInt(split[1]));
        } else if (command.equals("weapon_snippet_loss")) {
            configuration.put("weapon_snippet_loss", Integer.parseInt(split[1]));
        } else if (command.equals("weapon_paralysis_duration")) {
            configuration.put("weapon_paralysis_duration", Integer.parseInt(split[1]));
        }
    }

    protected Coordinate getStartCoordinate(int i) {
        return this.startCoordinates[i];
    }
}
