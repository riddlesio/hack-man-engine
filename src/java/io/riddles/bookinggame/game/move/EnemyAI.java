package io.riddles.bookinggame.game.move;

import io.riddles.bookinggame.game.data.Enemy;
import io.riddles.bookinggame.game.state.BookingGameState;

/**
 * Created by joost on 7/11/16.
 */
public interface EnemyAI {

    public Enemy transform(Enemy e, BookingGameState s);
}
