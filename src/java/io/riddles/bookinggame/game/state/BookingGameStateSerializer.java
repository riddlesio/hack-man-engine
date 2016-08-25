/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.bookinggame.game.state;

import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.bookinggame.game.move.BookingGameMove;
import io.riddles.javainterface.game.state.AbstractStateSerializer;

/**
 * io.riddles.catchfrauds.game.state.CatchFraudsStateSerializer - Created on 3-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGameStateSerializer extends AbstractStateSerializer<BookingGameState> {

    @Override
    public String traverseToString(BookingGameState state) {
        return visitState(state).toString();
    }

    @Override
    public JSONObject traverseToJson(BookingGameState state) throws NullPointerException {
        return visitState(state);
    }

    private JSONObject visitState(BookingGameState state) throws NullPointerException {
        JSONObject stateJson = new JSONObject();
        stateJson.put("round", state.getRoundNumber());

        BookingGameMove move = (BookingGameMove) state.getMoves().get(0);

        if (move.getException() == null) {
            stateJson.put("movetype", move.getMoveType());
            stateJson.put("exception", JSONObject.NULL);
            stateJson.put("board", state.getRepresentationString());
        } else {
            stateJson.put("movetype", move.getMoveType());
            stateJson.put("exception", move.getException().getMessage());
            stateJson.put("board", state.getRepresentationString());
        }

        return stateJson;
    }
}
