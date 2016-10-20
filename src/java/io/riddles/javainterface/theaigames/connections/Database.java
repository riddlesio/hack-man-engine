/*
 *  Copyright 2016 riddles.io (developers@riddles.io)
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 *      For the full copyright and license information, please view the LICENSE
 *      file that was distributed with this source code.
 */

package io.riddles.javainterface.theaigames.connections;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * io.riddles.javainterface.theaigames.connections.Database - Created on 15-9-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public final class Database {

    public static DB db = null;

    private Database() {}

    /**
     * Connects to MongoDB. Properties file must be included in
     * build path for this to work
     * @throws UnknownHostException
     * @throws NumberFormatException
     */
    public static void connectToDatabase() {

        Properties prop = new Properties();
        String dbHost, dbPort, dbUser, dbPassword, dbName;

        // read properties file that contains the database connection information
        try {
            File jarPath = new File(Amazon.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            String propertiesPath = jarPath.getParentFile().getAbsolutePath();
            prop.load(new FileInputStream(propertiesPath + "/database.properties"));
//			prop.load(new FileInputStream("database.properties")); //FOR ECLIPSE

            dbHost = prop.getProperty("host");
            dbPort = prop.getProperty("port");
            dbUser = prop.getProperty("username");
            dbPassword = prop.getProperty("password");
            dbName = prop.getProperty("dbname");
        } catch (Exception ex) {
            throw new RuntimeException("Couldn't read properties file");
        }

        // connect to MongoDB
        try {
            db = new MongoClient(dbHost, Integer.parseInt(dbPort)).getDB(dbName);
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        }

        if(db != null && db.authenticate(dbUser, dbPassword.toCharArray()))
            System.out.println("Successfully logged in to the database");
        else
            throw new RuntimeException("Log-in to the database failed");

    }

    /**
     * Stores everything for the game in the database
     * @param gameIdString : ID string of this game
     * @param winnerId : winner ID of the game
     * @param score : score of the game
     * @param savedFilePath : path of the file for visualization on Amazon S3
     * @param errors : DBObject that contains the paths for each bot's error log
     * @param dumps : DBObject that contains the paths for each bot's dump
     */
    public static void storeGameInDatabase(String gameIdString, ObjectId winnerId,
                                           int score, String savedFilePath,
                                           BasicDBObject errors, BasicDBObject dumps) {

        DBCollection coll = db.getCollection("games");

        DBObject queryDoc = new BasicDBObject()
                .append("_id", new ObjectId(gameIdString));

        DBObject updateDoc = new BasicDBObject()
                .append("$set", new BasicDBObject()
                        .append("winner", winnerId)
                        .append("score", score)
                        .append("visualization", savedFilePath)
                        .append("errors",errors)
                        .append("dump", dumps)
                        .append("ranked", 0)
                );

        coll.findAndModify(queryDoc, updateDoc);
    }
}