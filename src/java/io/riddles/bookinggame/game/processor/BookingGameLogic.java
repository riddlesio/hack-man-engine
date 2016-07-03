package io.riddles.bookinggame.game.processor;

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

    public Board transformBoard(Board b, BookingGameMove move) throws InvalidInputException {

        BookingGamePlayer p = move.getPlayer();
        int pId = p.getId();
        Coordinate c = b.getPlayerCoordinate(pId);
        System.out.println("transformBoard " + c);

        return b;
    }
}
