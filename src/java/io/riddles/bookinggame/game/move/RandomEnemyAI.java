package io.riddles.bookinggame.game.move;

import io.riddles.bookinggame.game.data.*;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.util.Random;

/**
 * Created by joost on 7/11/16.
 */
public class RandomEnemyAI implements EnemyAI {

    public Enemy transform(Enemy e, BookingGameState s) {
        Enemy newE = e;
        /* TODO: Make this intelligent (i.e. follow a nearby player) */


        /* Go around corners */
        MoveType d = getRandomDirection();
        MoveType oldDirection = newE.getDirection();
        while (d == getOppositeDirection(newE.getDirection())) {
            d = getRandomDirection();
        }
        newE.setDirection(d);

        /* Bump into wall */
        Coordinate newC = getMovedCoordinate(newE.getCoordinate(), newE.getDirection(), s.getBoard());
        if (!s.getBoard().isEmptyComplete(newC)) {
            System.out.println(e.toString() +  " bump into wall");
            int wallCounter = 0;
            if (!isEmptyInDirection(newE.getCoordinate(), MoveType.DOWN, s.getBoard())) wallCounter++;
            if (!isEmptyInDirection(newE.getCoordinate(), MoveType.UP, s.getBoard())) wallCounter++;
            if (!isEmptyInDirection(newE.getCoordinate(), MoveType.LEFT, s.getBoard())) wallCounter++;
            if (!isEmptyInDirection(newE.getCoordinate(), MoveType.RIGHT, s.getBoard())) wallCounter++;
            if (wallCounter > 2) { /* Dead end, go opposite direction */
                System.out.println("Dead end!!");
                d = getOppositeDirection(oldDirection);
                newE.setDirection(d);
                newC = getMovedCoordinate(newE.getCoordinate(), newE.getDirection(), s.getBoard());
            } else {
                /* Field blocked, find a new direction */
                while (!isEmptyInDirection(newE.getCoordinate(), newE.getDirection(), s.getBoard())) {
                    System.out.println("!isEmptyInDirection");
                    d = getRandomDirection();
                    while (d == getOppositeDirection(oldDirection)) {
                        System.out.println("getRandomDirection");
                        d = getRandomDirection();
                    }
                    newE.setDirection(d);
                    if (s.getBoard().isEmptyComplete(getMovedCoordinate(newE.getCoordinate(), newE.getDirection(), s.getBoard()))) {
                        newC = getMovedCoordinate(newE.getCoordinate(), newE.getDirection(), s.getBoard());
                    }
                }
            }
        }
        newE.setCoordinate(newC);
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

    public Boolean isEmptyInDirection(Coordinate c, MoveType m, BookingGameBoard b) {
        switch(m) {
            case UP:
                return (b.isEmptyComplete(new Coordinate(c.getX(), c.getY()-1)));
            case DOWN:
                return (b.isEmptyComplete(new Coordinate(c.getX(), c.getY()+1)));
            case RIGHT:
                return (b.isEmptyComplete(new Coordinate(c.getX()+1, c.getY())));
            case LEFT:
                return (b.isEmptyComplete(new Coordinate(c.getX()-1, c.getY())));
        }
        return true;
    }


    public MoveType getRandomDirection() {
        Random random = new Random();
        int a = random.nextInt(4);
        switch(a) {
            case 0:
                return MoveType.LEFT;
            case 1:
                return MoveType.RIGHT;
            case 2:
                return MoveType.UP;
            default:
                return MoveType.DOWN;
        }
    }

    public MoveType getOppositeDirection(MoveType d) {
        switch(d) {
            case LEFT:
                return MoveType.RIGHT;
            case RIGHT:
                return MoveType.LEFT;
            case UP:
                return MoveType.DOWN;
            default:
                return MoveType.UP;
        }
    }
}
