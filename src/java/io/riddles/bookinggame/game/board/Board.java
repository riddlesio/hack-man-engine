package io.riddles.bookinggame.game.board;

import java.awt.*;
import java.util.ArrayList;

/**
 * io.riddles.bookinggame.game.board.Board - Created on 29-6-16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class Board {
    protected String[][] field;
    protected int width = 20;
    protected int height = 11;


    protected Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.field = new String[width][height];
    }

    protected Board(int width, int height, String fieldLayout) {
        this.width = width;
        this.height = height;
        this.field = new String[width][height];

        String[] split = fieldLayout.split(",");
        int x = 0;
        int y = 0;

        for (String value : split) {
            this.field[x][y] = value;
            if (++x == width) {
                x = 0;
                y++;
            }
        }
    }

    protected Board(int width, int height, String[][] field) {
        this.width = width;
        this.height = height;
        this.field = field;
    }

    public String toString() {
        String s = "";
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                s += this.field[x][y];
            }
        }
        return s;
    }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }


    public String getFieldAt(Point c) {
        return field[c.x][c.y];
    }

    public void setFieldAt(Point c, String s) {
        field[c.x][c.y] = s;
    }

    public boolean isCoordinateValid(Point coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;

        return x >= 0 && y >= 0 && x < this.width && y < this.height
                && !this.field[x][y].equals("x");
    }

    public void dump() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.err.print(field[x][y]);
            }
            System.err.println();
        }
    }


    public int getNrAvailableFields() {
        int availableFields = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if(field[x][y].equals(".")) {
                    availableFields++;
                }
            }
        }
        return availableFields;
    }

    public ArrayList<Point> getValidNeighbors(Point coordinate) {
        ArrayList<Point> coordinates = new ArrayList<>();
        coordinates.add(coordinate);
        return getValidNeighbors(coordinates);
    }

    public ArrayList<Point> getValidNeighbors(ArrayList<Point> coordinates) {
        ArrayList<Point> neighbors = new ArrayList<>();

        for (Point coordinate : coordinates) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (Math.abs(dx + dy) != 1) continue; // skip diagonal neighbors

                    Point neighbor = new Point(coordinate.x + dx, coordinate.y + dy);
                    boolean exists = neighbors.stream().anyMatch(p -> p.equals(neighbor));

                    if (!exists && isCoordinateValid(neighbor)) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }

        return neighbors;
    }
}
