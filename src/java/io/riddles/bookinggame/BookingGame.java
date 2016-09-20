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

package io.riddles.bookinggame;

import io.riddles.bookinggame.engine.BookingGameEngine;

/**
 * io.riddles.bookinggame.BookingGame - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class BookingGame {

    public static void main(String[] args) throws Exception {
        BookingGameEngine engine;

        if (args.length > 0) { // Create aigames engine
            engine = new BookingGameEngine(args);
        } else {
            engine = new BookingGameEngine();
        }

        engine.run();
    }
}