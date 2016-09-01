package io.riddles.bookinggame.game.board;

import io.riddles.bookinggame.engine.BookingGameEngine;
import io.riddles.bookinggame.game.enemy.Enemy;
import io.riddles.bookinggame.game.player.BookingGamePlayer;

import java.awt.*;
import java.util.ArrayList;
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
    private ArrayList<Enemy> enemies;

    public BookingGameBoard(int width, int height, ArrayList<BookingGamePlayer> players) {
        super(width, height);
        this.players = players;
        this.enemies = new ArrayList<>();
    }

    public BookingGameBoard(int width, int height,
                            String fieldLayout, ArrayList<BookingGamePlayer> players) {
        super(width, height, fieldLayout);
        this.players = players;
        this.enemies = new ArrayList<>();
    }

    public BookingGameBoard(int width, int height,
                            ArrayList<BookingGamePlayer> players,
                            ArrayList<Enemy> enemies, String[][] field) {
        super(width, height, field);
        this.players = players;
        this.enemies = enemies;
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

        ArrayList<Enemy> clonedEnemies = this.enemies.stream()
                .map(Enemy::clone)
                .collect(Collectors.toCollection(ArrayList::new));

        return new BookingGameBoard(this.width, this.height, clonedPlayers,
                clonedEnemies, clonedField);
    }

    public String toRepresentationString() {
        String output = "";
        int counter = 0;

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String s = this.field[x][y];

                for (BookingGamePlayer player : this.players) {
                    if (player.getCoordinate().getX() == x && player.getCoordinate().getY() == y) {
                        s = String.valueOf(player.getId());
                    }
                }
                for (Enemy enemy : this.enemies) {
                    if (enemy.getCoordinate().getX() == x && enemy.getCoordinate().getY() == y) {
                        s = "E";
                    }
                }

                if (counter > 0) {
                    output += ",";
                }
                output += s;
                counter++;
            }
        }

        return output;
    }

    public void updatePlayerMovements() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                for (BookingGamePlayer player : this.players) {
                    if (this.field[x][y].equals(player.getId() + "")) {
                        this.field[x][y] = ".";
                    }
                    if (player.getCoordinate().getX() == x && player.getCoordinate().getY() == y) {
                        this.field[x][y] = player.getId() + "";
                    }
                }
            }
        }
    }

    public void updateEnemyMovements() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.field[x][y].equals("E")) {
                    this.field[x][y] = ".";
                }
                for (Enemy enemy : this.enemies) {
                    if (enemy.getCoordinate().getX() == x && enemy.getCoordinate().getY() == y) {
                        this.field[x][y] = "E";
                    }
                }
            }
        }
    }

    public Boolean isEmpty(Point coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;
        String cell = this.field[x][y];

        //noinspection SimplifiableIfStatement
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            return false;
        }

        return (cell.equals(".") || cell.equals("C") || cell.equals("W"));
    }

    /* Returns coordinate of empty field furthest away from all players */
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
        ArrayList<Point> emptyLocations = getEmptyLocations();
        if (emptyLocations.size() <= 0) {
            System.err.println("Can't spawn snippets, no empty field available.");
            return;
        }

        Point spawnLocation = emptyLocations.get(
                BookingGameEngine.RANDOM.nextInt(emptyLocations.size()));
        addSnippet(spawnLocation);
    }

    public boolean addSnippet(Point c) {
        if (this.field[c.x][c.y].equals(".")) {
            this.field[c.x][c.y] = "C";
            return true;
        }
        return false;
    }

    public Point getEnemyStartField() {
        Point c = new Point(0, 0);
        int y = this.height / 2;

        for (int x = this.width / 2 - 1; x < this.width; x++) {
            if (this.field[x][y].equals(".")) {
                c = new Point(x, y);
                return c;
            }
        }

        return c;
    }

    public void dump() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(this.field[x][y]);
            }
            System.out.println();
        }
    }

    public ArrayList<Enemy> getEnemies() {
        return this.enemies;
    }

    public ArrayList<BookingGamePlayer> getPlayers() {
        return this.players;
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    public void killEnemyAt(Point coordinate) {
        this.enemies.stream()
                .filter(enemy -> enemy.getCoordinate().equals(coordinate))
                .forEach(enemy -> this.enemies.remove(enemy));
    }

    private ArrayList<Point> getLocationOf(String type) {
        ArrayList<Point> snippetLocations = new ArrayList<>();

        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.field[x][y].equals(type)) {
                    snippetLocations.add(new Point(x, y));
                }
            }
        }

        return snippetLocations;
    }

    public ArrayList<Point> getEmptyLocations() {
        return getLocationOf(".");
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
