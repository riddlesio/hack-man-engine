package io.riddles.bookinggame

import io.riddles.bookinggame.game.data.MoveType
import io.riddles.bookinggame.game.move.BookingGameMove
import io.riddles.bookinggame.game.move.BookingGameMoveDeserializer
import io.riddles.bookinggame.game.player.BookingGamePlayer
import io.riddles.javainterface.exception.InvalidInputException
import spock.lang.Specification

class javainterfaceTests extends Specification {
    def "BookingGameMoveDeserializer must return one of MoveType when receiving valid input"() {
        println("BookingGameMove")

        given:
        BookingGamePlayer player = new BookingGamePlayer(1);
        BookingGameMoveDeserializer deserializer = new BookingGameMoveDeserializer(player);


        expect:
        BookingGameMove move = deserializer.traverse(input);
        result == move.getMoveType();


        where:
        input   | result
        "up"        | MoveType.UP
        "down"      | MoveType.DOWN
        "right"     | MoveType.RIGHT
        "left"      | MoveType.LEFT
        "pass"      | MoveType.PASS
    }

    def "BookingGameMoveDeserializer must throw an InvalidInputException when receiving unexpected input"() {

        given:
        BookingGamePlayer player = new BookingGamePlayer(1);
        BookingGameMoveDeserializer deserializer = new BookingGameMoveDeserializer(player);

        when:
        BookingGameMove move = deserializer.traverse("updown");

        then:
        move.getException() == InvalidInputException;
    }
}