package io.riddles.bookinggame.game.processor;

import io.riddles.bookinggame.game.data.BookingGameBoard;
import io.riddles.bookinggame.game.data.MoveType;
import io.riddles.bookinggame.game.player.BookingGamePlayer;

import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.bookinggame.game.data.Coordinate;
import io.riddles.bookinggame.game.data.Board;
import io.riddles.javainterface.exception.InvalidInputException;

/**
 * Created by joost on 3-7-16.
 */
public class BookingGameLogic {


    public BookingGameLogic() {
    }

    public BookingGameBoard transformBoard(BookingGameBoard b, BookingGameMove move) throws InvalidInputException {

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
                p.updateSnippets(-3);
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

        return b;
    }
}
