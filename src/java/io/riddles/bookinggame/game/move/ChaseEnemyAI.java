package io.riddles.bookinggame.game.move;

import io.riddles.bookinggame.game.data.*;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.util.List;
import java.util.Random;

/**
 * Created by joost on 7/11/16.
 */
public class ChaseEnemyAI implements EnemyAI {

    public Enemy transform(Enemy e, BookingGameState s) {
        Enemy newE = e;

        MoveType oldDirection = newE.getDirection();


        int wallCounter = 0;
        if (!isEmptyInDirection(newE.getCoordinate(), MoveType.DOWN, s.getBoard())) wallCounter++;
        if (!isEmptyInDirection(newE.getCoordinate(), MoveType.UP, s.getBoard())) wallCounter++;
        if (!isEmptyInDirection(newE.getCoordinate(), MoveType.LEFT, s.getBoard())) wallCounter++;
        if (!isEmptyInDirection(newE.getCoordinate(), MoveType.RIGHT, s.getBoard())) wallCounter++;

        //Boolean makeDecision = false;
        //if (wallCounter < 3) makeDecision = true;

        MoveType newDirection = getChaseDirection(newE.getCoordinate(), newE.getPreviousCoordinate(), s.getBoard());














        if (isEmptyInDirection(newE.getCoordinate(), newDirection, s.getBoard())) {
            if (newDirection != getOppositeDirection(oldDirection) || wallCounter == 3) {
                newE.setDirection(newDirection);
            } else {
                System.out.println(" Can't go " + newDirection);

            }
        }



        Coordinate newC = getMovedCoordinate(newE.getCoordinate(), newE.getDirection(), s.getBoard());
        if (s.getBoard().isEmpty(newC)) {
            newE.setCoordinate(newC);
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

    public Boolean isEmptyInDirection(Coordinate c, MoveType m, BookingGameBoard b) {
        switch(m) {
            case UP:
                return (b.isEmpty(new Coordinate(c.getX(), c.getY()-1)));
            case DOWN:
                return (b.isEmpty(new Coordinate(c.getX(), c.getY()+1)));
            case RIGHT:
                return (b.isEmpty(new Coordinate(c.getX()+1, c.getY())));
            case LEFT:
                return (b.isEmpty(new Coordinate(c.getX()-1, c.getY())));
        }
        return true;
    }

    private MoveType getChaseDirection(Coordinate c, Coordinate prevC, BookingGameBoard b) {
        Map map = new Map(b.getWidth(), b.getHeight());
        map.initFromBoard(b, prevC);
        map.drawMap();
        Coordinate newC = getNearestPlayerCoordinate(map, c, b);

        System.out.println( "c: " + c + " getNearestPlayerCoordinate    " + newC);

        if (c.getX() != newC.getX() || c.getY() != newC.getY()) {
            List<Node> l = map.findPath(c, newC);
            if (l.size() > 0) {
                return getDirection(c, new Coordinate(l.get(0).getxPosition(), l.get(0).getyPosition()));
            }
        }
        System.out.println("getChaseDirection FAILED");
        return null;
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

    private MoveType getOppositeDirection(MoveType d) {
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

    private MoveType getDirection(Coordinate oldC, Coordinate newC) {
        if (newC.getX() > oldC.getX()) return MoveType.RIGHT;
        if (newC.getX() < oldC.getX()) return MoveType.LEFT;
        if (newC.getY() > oldC.getY()) return MoveType.DOWN;
        if (newC.getY() < oldC.getY()) return MoveType.UP;
        return null;
    }

    private Coordinate getNearestPlayerCoordinate(Map map, Coordinate c, BookingGameBoard b) {
        int nearestX = 0, nearestY = 0;
        int smallestDistance = Integer.MAX_VALUE;
        for (int y = 0; y < b.getHeight(); y++) {
            for (int x = 0; x < b.getWidth(); x++) {
                if (isInteger(b.getFieldAtComplete(new Coordinate(x,y)))) {



                    Coordinate newC = new Coordinate(x, y);


                    if (c.getX() != newC.getX() || c.getY() != newC.getY()) {
                        List<Node> l = map.findPath(c, newC);
                        if (l.size() < smallestDistance && l.size() > 1 ) {
                            nearestX = x; nearestY = y; smallestDistance = l.size();
                            System.out.println( "c: " + c + " checkNPC    " + newC + " dist: " + l.size());

                        }

                    }
                }
            }
        }
        return new Coordinate(nearestX, nearestY);
    }

    private boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
