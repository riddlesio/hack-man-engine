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

package io.riddles.javainterface.theaigames.io;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Stack;

import io.riddles.javainterface.io.IO;

/**
 * io.riddles.javainterface.theaigames.io.AIGamesIOHandler - Created on 15-9-16
 *
 * Fakes being the wrapper, sends the expected stuff to the engine.
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class AIGamesIOHandler implements IO {

    private Stack<String> inputMessages;

    public AIGamesIOHandler(String[] args) {
        this.inputMessages = new Stack<>();
        int botCount;

        try {  // check if actual game for aigames
            new ObjectId(args[0]);
            botCount = (args.length - 1) / 2;
        } catch (IllegalArgumentException ex) {
            botCount = args.length;
        }

        String botIds = "bot_ids ";
        for (int i = 0; i < botCount; i++) botIds += i + ",";
        botIds = botIds.substring(0, botIds.length() - 1);

        this.inputMessages.push("start");
        this.inputMessages.push(botIds);
    }

    @Override
    public void sendMessage(String message) {}

    @Override
    public void waitForMessage(String expected) {}

    @Override
    public String getNextMessage() throws IOException {
        return this.inputMessages.pop();
    }
}
