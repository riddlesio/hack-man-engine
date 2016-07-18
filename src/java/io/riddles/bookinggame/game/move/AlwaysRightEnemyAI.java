package io.riddles.bookinggame.game.move;

import io.riddles.bookinggame.game.data.*;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.util.Random;

/**
 * Created by joost on 7/18/16.
 */


public class AlwaysRightEnemyAI implements EnemyAI {

    public Enemy transform(Enemy e, BookingGameState s) {
        Enemy newE = e;

        MoveType d = MoveType.RIGHT;
        newE.setDirection(d);

        Coordinate newC = getMovedCoordinate(newE.getCoordinate(), newE.getDirection(), s.getBoard());
        if (s.getBoard().isEmptyComplete(newC)) {
            newE.setCoordinate(newC);
        } else {
            System.out.println(e.toString() +  " bump into wall");
        }
        return newE;
    }



    public Coordinate getMovedCoordinate(Coordinate c, MoveType m, Board b) {
        switch(m) {
            case UP:
                return new Coordinate(c.getX(), c.getY()-1);
            case DOWN:
                return new Coordinate(c.getX(), c.getY()+1);
            case RIGHT:
                return new Coordinate(c.getX()+1, c.getY());
            case LEFT:
                return new Coordinate(c.getX()-1, c.getY());
        }
        return c;
    }
}
