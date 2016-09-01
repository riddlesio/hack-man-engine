package io.riddles.bookinggame.game.enemy;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;

import io.riddles.bookinggame.BookingGame;
import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.board.ShortestPathHandler;
import io.riddles.bookinggame.game.move.MoveType;
import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;

/**
 * io.riddles.bookinggame.game.enemy.ChaseWithChanceEnemyAI - Created on 31-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class ChaseWithChanceEnemyAI extends AbstractEnemyAI {

    private double pInitial;
    private double pGrowth;
    private ShortestPathHandler shortestPathHandler;

    public ChaseWithChanceEnemyAI(double pInitial, double pGrowth) {
        this.pInitial = pInitial;
        this.pGrowth = pGrowth;
    }

    @Override
    public Point transform(Enemy enemy, BookingGameState state) {
        Point mandatoryTransform = mandatoryTranform(enemy);
        if (mandatoryTransform != null) {
            // Directions that are mandatory will always be taken
            return mandatoryTransform;
        }

        if (this.shortestPathHandler == null) {
            this.shortestPathHandler = new ShortestPathHandler(state.getBoard());
        }

        double pChase = getPChase(state.getRoundNumber());
        if (BookingGameEngine.RANDOM.nextDouble() <= pChase) {  // chase with chance pChase
            return getShortestPathTransform(enemy, state.getBoard().getPlayers());
        }

        // otherwise go random direction
        ArrayList<MoveType> availableDirections = getAvailableDirections(enemy, state.getBoard());
        return getRandomAvailableTransform(availableDirections, enemy.getCoordinate());
    }

    private double getPChase(int roundNumber) {
        double pChase = this.pInitial + ((roundNumber - 1) * this.pGrowth);

        if (pChase > 1.0) return 1.0;
        if (pChase < 0.0) return 0.0;
        return pChase;
    }

    private Point getRandomAvailableTransform(ArrayList<MoveType> availableDirections,
                                              Point coordinate) {
        MoveType moveType = availableDirections.get(
                BookingGameEngine.RANDOM.nextInt(availableDirections.size()));
        return getMovedCoordinate(coordinate, moveType);
    }

    private Point getShortestPathTransform(Enemy enemy, ArrayList<BookingGamePlayer> players) {
        ArrayList<Point> blockedMoves = new ArrayList<>();
        Point oppositeMove = getMovedCoordinate(
                enemy.getCoordinate(), enemy.getDirection().getOppositeMoveType());
        blockedMoves.add(oppositeMove);

        // Get shortest path to closest player
        ArrayList<Point> shortestPath = new ArrayList<>();
        for (BookingGamePlayer player : players) {
            ArrayList<Point> toPlayerPath = this.shortestPathHandler.getShortestPath(
                    enemy.getCoordinate(), player.getCoordinate(), blockedMoves);
            if (toPlayerPath.size() > shortestPath.size()) {
                shortestPath = toPlayerPath;
            }
        }

        return shortestPath.get(0);
    }
}
