package io.riddles.bookinggame.game.data;

import io.riddles.javainterface.exception.InvalidInputException;

/**
 * Created by joost on 29-6-16.
 */
public class Board {
    protected String[][] fields;
    private int width = 20;
    private int height = 11;


    public Board(int w, int h) {
        this.width = w;
        this.height = h;
        this.fields = new String[w][h];
    }

    public String toString() {
        String s = "";
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                s += fields[x][y];
            }
        }
        return s;
    }

    public void initialiseFromString(String input, int w, int h) {
        String[] s = input.split(",");
        this.width = w;
        this.height = h;
        this.fields = new String[w][h];
        int x = 0, y = 0;
        for (int i = 0; i < s.length; i++) {
            this.fields[x][y] = s[i];
            if (++x == w) {
                x = 0; y++;
            }
        }
    }

    public Coordinate getPlayerCoordinate(int pId) throws InvalidInputException {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (fields[x][y] == String.valueOf(pId)) {
                    return new Coordinate (x, y);
                }
            }
        }
        throw new InvalidInputException("Player not found on Board");
    }

}
