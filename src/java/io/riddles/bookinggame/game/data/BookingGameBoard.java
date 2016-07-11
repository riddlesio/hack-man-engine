package io.riddles.bookinggame.game.data;

import io.riddles.bookinggame.game.player.BookingGamePlayer;
import io.riddles.bookinggame.game.state.BookingGameState;

import java.util.ArrayList;

/**
 * Created by joost on 7/11/16.
 */
public class BookingGameBoard extends Board {

    public BookingGameBoard(int w, int h) {
        super(w, h);
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
}
