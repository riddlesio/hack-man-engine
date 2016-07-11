package io.riddles.bookinggame.game.move;

import io.riddles.bookinggame.game.data.Coordinate;
import io.riddles.bookinggame.game.state.BookingGameState;

/**
 * Created by joost on 7/11/16.
 */
public interface EnemyAI {

    public Coordinate transform(Coordinate c, BookingGameState s);
}
