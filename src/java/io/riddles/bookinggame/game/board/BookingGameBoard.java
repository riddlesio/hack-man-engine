package io.riddles.bookinggame.game.board;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.enemy.BookingGameEnemy;
import io.riddles.bookinggame.game.enemy.EnemyAIInterface;
import io.riddles.bookinggame.game.move.MoveType;
import io.riddles.bookinggame.game.player.BookingGamePlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * io.riddles.bookinggame.game.board.Board - Created on 11-7-16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class BookingGameBoard extends Board {

    private ArrayList<BookingGamePlayer> players;
    private ArrayList<BookingGameEnemy> enemies;
    private ArrayList<Point> enemySpawnPoints;
    private ArrayList<Point> playerInaccessablePoints;

    public BookingGameBoard(int width, int height, String fieldLayout,
                            Point[] enemySpawnPoints,
                            ArrayList<BookingGamePlayer> players) {
        super(width, height, fieldLayout);
        this.players = players;
        this.enemies = new ArrayList<>();
        this.enemySpawnPoints = new ArrayList<>(Arrays.asList(enemySpawnPoints));
        this.playerInaccessablePoints = getValidNeighbors(this.enemySpawnPoints);
    }

    private BookingGameBoard(int width, int height, ArrayList<Point> enemySpawnPoints,
                             ArrayList<Point> playerInaccessablePoints,
                             ArrayList<BookingGamePlayer> players,
                             ArrayList<BookingGameEnemy> enemies, String[][] field) {
        super(width, height, field);
        this.players = players;
        this.enemies = enemies;
        this.enemySpawnPoints = enemySpawnPoints;
        this.playerInaccessablePoints = playerInaccessablePoints;
    }

    public BookingGameBoard clone() {
        String[][] clonedField = new String[this.width][this.height];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                clonedField[x][y] = this.field[x][y];
            }
        }

        ArrayList<BookingGamePlayer> clonedPlayers = this.players.stream()
                .map(BookingGamePlayer::clone)
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<BookingGameEnemy> clonedEnemies = this.enemies.stream()
                .map(BookingGameEnemy::clone)
                .collect(Collectors.toCollection(ArrayList::new));

        return new BookingGameBoard(this.width, this.height, this.enemySpawnPoints,
                this.playerInaccessablePoints, clonedPlayers, clonedEnemies, clonedField);
    }

    public String toString() {
        String output = "";

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String cell = "";

                for (BookingGamePlayer player : this.players) {
                    if (player.getCoordinate().x == x && player.getCoordinate().y == y) {
                        cell += player.getId() + "";
                    }
                }
                for (BookingGameEnemy enemy : this.enemies) {
                    if (enemy.getCoordinate().x == x && enemy.getCoordinate().y == y) {
                        cell += "E";
                    }
                }

                if (cell.length() <= 0) {
                    cell = this.field[x][y];
                }
                output += cell + ",";
            }
        }

        return output.substring(0, output.length() - 1);
    }

    public boolean isCoordinateValid(Point coordinate, boolean isForPlayer) {
        boolean isValid = isCoordinateValid(coordinate);

        if (!isForPlayer) {
            return isValid;
        }

        boolean playerInaccessable = this.playerInaccessablePoints.stream()
                .anyMatch(p -> p.equals(coordinate));

        return isValid && !playerInaccessable;
    }

    public boolean isOnSpawnPoint(Point coordinate) {
        return this.enemySpawnPoints.stream()
                .anyMatch(point -> point.equals(coordinate));
    }

    public boolean isOnSpawnGate(Point coordinate) {
        return this.playerInaccessablePoints.stream()
                .anyMatch(point -> point.equals(coordinate));
    }

    public Point getCoordinateAfterMove(Point coordinate, MoveType moveType) {
        if (moveType != null) {
            switch(moveType) {
                case UP:
                    return new Point(coordinate.x, coordinate.y - 1);
                case DOWN:
                    return new Point(coordinate.x, coordinate.y + 1);
                case RIGHT:
                    return new Point(coordinate.x + 1, coordinate.y);
                case LEFT:
                    return new Point(coordinate.x - 1, coordinate.y);
            }
        }
        return coordinate;
    }

    public boolean isEmptyInDirection(Point coordinate, MoveType moveType) {
        if (moveType == null) {
            return false;
        }

        Point newCoordinate = getCoordinateAfterMove(coordinate, moveType);
        return isCoordinateValid(newCoordinate);
    }

    /* Returns coordinate of empty field furthest away from all players */
    // not used at the moment
    public Point getLoneliestCoordinate() {
        Point coordinate = new Point(0,0);
        int score = 0;

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                int minDistance = Integer.MAX_VALUE;

                for (BookingGamePlayer player : this.players) {
                    Point playerC = player.getCoordinate();
                    int distance = Math.abs(x - playerC.x);

                    distance += Math.abs(y - playerC.y);
                    if (minDistance > distance) {
                        minDistance = distance;
                    }
                }

                if (minDistance > score && this.field[x][y].equals(".")) {
                    score = minDistance;
                    coordinate = new Point(x, y);
                }
            }
        }

        return coordinate;
    }

    public void addRandomSnippet() {
        addItem(getRandomEmptyPoint(), "C");
    }

    public void addRandomWeapon() {
        addItem(getRandomEmptyPoint(), "W");
    }

    private Point getRandomEmptyPoint() {
        ArrayList<Point> emptyLocations = getEmptyLocations();
        if (emptyLocations.size() <= 0) {
            System.err.println("No empty points on the map.");
            return null;
        }

        return emptyLocations.get(BookingGameEngine.RANDOM.nextInt(emptyLocations.size()));
    }

    private void addItem(Point coordinate, String type) {
        if (coordinate == null) return;

        int x = coordinate.x;
        int y = coordinate.y;
        if (this.field[x][y].equals(".")) {
            this.field[x][y] = type;
        }
    }

    public void spawnEnemy(EnemyAIInterface enemyAI) {
        int randomIndex = BookingGameEngine.RANDOM.nextInt(this.enemySpawnPoints.size());
        Point randomSpawnPoint = this.enemySpawnPoints.get(randomIndex);

        MoveType randomDirection = MoveType.getRandomMovingMoveType();
        BookingGameEnemy enemy = new BookingGameEnemy(randomSpawnPoint, randomDirection, enemyAI);
        this.enemies.add(enemy);
    }

    public void dump() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(this.field[x][y]);
            }
            System.out.println();
        }
    }

    public ArrayList<BookingGameEnemy> getEnemies() {
        return this.enemies;
    }

    public ArrayList<BookingGamePlayer> getPlayers() {
        return this.players;
    }

    public void killEnemy(BookingGameEnemy enemy) {
        this.enemies.remove(enemy);
    }

    private ArrayList<Point> getLocationOf(String type) {
        ArrayList<Point> itemLocation = new ArrayList<>();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.field[x][y].equals(type)) {
                    itemLocation.add(new Point(x, y));
                }
            }
        }

        return itemLocation;
    }

    // pretty crappy method, but works
    public ArrayList<Point> getEmptyLocations() {
        ArrayList<Point> emptyLocations = getLocationOf(".");

        ArrayList<Point> nonEmptyPoints = this.players.stream()
                .map(BookingGamePlayer::getCoordinate)
                .collect(Collectors.toCollection(ArrayList::new));

        nonEmptyPoints.addAll(this.enemies.stream()
                .map(BookingGameEnemy::getCoordinate)
                .collect(Collectors.toCollection(ArrayList::new)));

        ArrayList<Point> actualEmptyLocations = new ArrayList<>();
        for (Point p : nonEmptyPoints) {
            boolean found = false;

            for (Point taken : emptyLocations) {
                if (p.equals(taken)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                actualEmptyLocations.add(p);
            }
        }

        return actualEmptyLocations;
    }

    public ArrayList<Point> getSnippetLocations() {
        return getLocationOf("C");
    }

    public ArrayList<Point> getWeaponLocations() {
        return getLocationOf("W");
    }

    public BookingGamePlayer getPlayerById(int id) {
        for (BookingGamePlayer player : this.players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }
}
