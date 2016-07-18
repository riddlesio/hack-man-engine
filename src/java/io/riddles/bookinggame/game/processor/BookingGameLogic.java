package io.riddles.bookinggame.game.processor;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.data.*;
import io.riddles.bookinggame.game.player.BookingGamePlayer;

import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.bookinggame.game.state.BookingGameState;
import io.riddles.javainterface.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by joost on 3-7-16.
 */
public class BookingGameLogic {


    public BookingGameLogic() {
    }

    public BookingGameState transformBoard(BookingGameState state, BookingGameMove move, ArrayList<BookingGamePlayer> players) throws InvalidInputException {

        BookingGameBoard board = state.getBoard();
        transformPlayerLocation(board, move);
        transformAttack(state, move, players);

        return state;
    }

    private void transformPlayerLocation(BookingGameBoard b, BookingGameMove move) {
        BookingGamePlayer p = move.getPlayer();
        int pId = p.getId();
        Coordinate c = p.getCoordinate();
        Coordinate newC = c;

        switch(move.getMoveType()) {
            case UP:
                if (b.isEmpty(new Coordinate(c.getX(), c.getY()-1))) {
                    newC = new Coordinate(c.getX(), c.getY()-1);
                }
                break;
            case DOWN:
                if (b.isEmpty(new Coordinate(c.getX(), c.getY()+1))) {
                    newC = new Coordinate(c.getX(), c.getY()+1);
                }
                break;
            case RIGHT:
                if (b.isEmpty(new Coordinate(c.getX()+1, c.getY()))) {
                    newC = new Coordinate(c.getX()+1, c.getY());
                }
                break;
            case LEFT:
                if (b.isEmpty(new Coordinate(c.getX()-1, c.getY()))) {
                    newC = new Coordinate(c.getX()-1, c.getY());
                }
                break;
            case ATTACK:
                break;
        }

        switch (b.getFieldAt(newC)) {
            case "B": /* A bug */
                p.updateSnippets(-BookingGameEngine.configuration.get("enemy_snippet_loss"));
                break;
            case "C": /* Code snippet */
                b.setFieldAt(c, ".");
                p.updateSnippets(+1);
                break;
            case "W": /* A Weapon */
                b.setFieldAt(c, ".");
                p.updateWeapons(1);
                break;

        }
        p.setCoordinate(newC);
    }

    private void transformAttack(BookingGameState state, BookingGameMove move, ArrayList<BookingGamePlayer> players) {
        BookingGamePlayer player = move.getPlayer();
        BookingGameBoard board = state.getBoard();

        if (move.getMoveType() == MoveType.ATTACK) {
            if (player.getWeapons()>0) {
                player.updateWeapons(-1);
                    /* Kill enemies nearby */
                int prevSize = state.getEnemies().size();
                Iterator<Enemy> it = state.getEnemies().iterator();
                while (it.hasNext()) {
                    Enemy enemy = it.next();
                    if (Math.abs(enemy.getCoordinate().getX() - player.getCoordinate().getX()) + (Math.abs(enemy.getCoordinate().getY() - player.getCoordinate().getY())) < 2) {
                        board.setFieldAt(enemy.getCoordinate(), "-");
                        it.remove();
                    }
                }
                if (state.getEnemies().size() != prevSize) {
                        /* An enemy was killed */
                }
                    /* Paralyse players nearby and take N code snippets*/
                for (BookingGamePlayer otherPlayer : players) {
                    if (otherPlayer.getId() != player.getId()) {
                        if (Math.abs(otherPlayer.getCoordinate().getX() - player.getCoordinate().getX()) + (Math.abs(otherPlayer.getCoordinate().getY() - player.getCoordinate().getY())) < 2) {
                            otherPlayer.paralyse(BookingGameEngine.configuration.get("weapon_paralysis_duration"));
                            otherPlayer.updateSnippets(-BookingGameEngine.configuration.get("weapon_snippet_loss"));
                            System.out.println("ATTACK: " + otherPlayer);
                        }
                    }
                }
            }
        }
    }
}
