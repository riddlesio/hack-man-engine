package io.riddles.bookinggame.game.enemy;

import java.awt.*;

import io.riddles.bookinggame.game.state.BookingGameState;

/**
 * io.riddles.bookinggame.game.enemy.EnemyInterface - Created on 29-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public interface EnemyAIInterface {

    Point transform(BookingGameEnemy enemy, BookingGameState state);
}
