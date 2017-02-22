package io.riddles.bookinggame.game.enemy;

import java.awt.*;
import java.util.ArrayList;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.board.BookingGameBoard;
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
    public Point transform(BookingGameEnemy enemy, BookingGameState state) {
        BookingGameBoard board = state.getBoard();
        ArrayList<MoveType> availableDirections = getAvailableDirections(enemy, board);

        Point mandatoryTransform = mandatoryTranform(enemy, board, availableDirections);
        if (mandatoryTransform != null) {
            // Directions that are mandatory will always be taken
            return mandatoryTransform;
        }

        if (this.shortestPathHandler == null) {
            this.shortestPathHandler = new ShortestPathHandler(board);
        }

        double pChase = getPChase(state.getRoundNumber());
        if (BookingGameEngine.RANDOM.nextDouble() <= pChase) {  // chase with chance pChase
            Point transform = getShortestPathTransform(enemy, board);

            if (transform != null) {
                return transform;
            }
        }

        // otherwise go random direction
        return getRandomAvailableTransform(availableDirections, enemy.getCoordinate(), board);
    }

    private double getPChase(int roundNumber) {
        double pChase = this.pInitial + ((roundNumber - 1) * this.pGrowth);

        if (pChase > 1.0) return 1.0;
        if (pChase < 0.0) return 0.0;
        return pChase;
    }

    private Point getRandomAvailableTransform(ArrayList<MoveType> availableDirections,
                                              Point coordinate, BookingGameBoard board) {
        MoveType moveType = availableDirections.get(
                BookingGameEngine.RANDOM.nextInt(availableDirections.size()));
        return board.getCoordinateAfterMove(coordinate, moveType);
    }

    private Point getShortestPathTransform(BookingGameEnemy enemy, BookingGameBoard board) {
        ArrayList<Point> blockedMoves = new ArrayList<>();
        Point oppositeMove = board.getCoordinateAfterMove(
                enemy.getCoordinate(), enemy.getDirection().getOppositeMoveType());
        blockedMoves.add(oppositeMove);

        // Get shortest path to closest player
        ArrayList<Point> shortestPath = null;
        for (BookingGamePlayer player : board.getPlayers()) {
            ArrayList<Point> toPlayerPath = this.shortestPathHandler.getShortestPath(
                    enemy.getCoordinate(), player.getCoordinate(), blockedMoves);
            if (shortestPath == null ||
                    (toPlayerPath != null && toPlayerPath.size() < shortestPath.size())) {
                shortestPath = toPlayerPath;
            }
        }

        if (shortestPath == null || shortestPath.size() <= 0) return null;

        return shortestPath.get(0);
    }
}
