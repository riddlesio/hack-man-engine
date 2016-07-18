package io.riddles.bookinggame.game.data;

import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.util.ArrayList;

/**
 * Created by joost on 7/11/16.
 */
public class BookingGameBoard extends Board {
    protected String[][] fieldsComplete;

    public BookingGameBoard(int w, int h) {

        super(w, h);
        fieldsComplete = new String[w][h];
    }

    public void dump(ArrayList<BookingGamePlayer> players, BookingGameState state) {
        ArrayList<Enemy> enemies = state.getEnemies();
        for (int i = 0; i < enemies.size(); i++) {
            System.out.println(enemies.get(i));
        }
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String s = fields[x][y];
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getCoordinate().getX() == x && players.get(i).getCoordinate().getY() == y) {
                        s = String.valueOf(players.get(i).getId());
                    }
                }
                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i).getCoordinate().getX() == x && enemies.get(i).getCoordinate().getY() == y) {
                        s = "E";
                    }
                }
                System.out.print(s);
            }
            System.out.println();
        }
    }

    public String toRepresentationString(ArrayList<BookingGamePlayer> players, BookingGameState state) {
        ArrayList<Enemy> enemies = state.getEnemies();
        String output = "";
        int counter = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                String s = fields[x][y];
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getCoordinate().getX() == x && players.get(i).getCoordinate().getY() == y) {
                        s = String.valueOf(players.get(i).getId());
                    }
                }
                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i).getCoordinate().getX() == x && enemies.get(i).getCoordinate().getY() == y) {
                        s = "E";
                    }
                }
                if (counter > 0) output += ",";
                output += s;
                counter++;
            }
        }
        return output;
    }

    public void updateComplete(ArrayList<BookingGamePlayer> players, BookingGameState state) {
        ArrayList<Enemy> enemies = state.getEnemies();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                fieldsComplete[x][y] = fields[x][y];
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getCoordinate().getX() == x && players.get(i).getCoordinate().getY() == y) {
                        fieldsComplete[x][y] = String.valueOf(players.get(i).getId());
                    }
                }
                for (int i = 0; i < enemies.size(); i++) {
                    if (enemies.get(i).getCoordinate().getX() == x && enemies.get(i).getCoordinate().getY() == y) {
                        fieldsComplete[x][y] = "E";
                    }
                }
            }
        }
    }

    public Boolean isEmptyComplete(Coordinate c) {
        if (c.getX() < 0 || c.getY() < 0 || c.getX() >= this.width || c.getY() >= this.height) {
            return false;
        }
         return (!fieldsComplete[c.getX()][c.getY()].equals("x"));
    }

    public String toStringComplete() {
        String s = "";
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                s += fieldsComplete[x][y];
            }
        }
        return s;
    }
}
