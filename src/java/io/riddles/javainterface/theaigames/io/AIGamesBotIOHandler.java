package io.riddles.javainterface.theaigames.io;

import java.io.IOException;
import java.io.OutputStreamWriter;

import io.riddles.javainterface.io.BotIO;

/**
 * io.riddles.javainterface.theaigames.io.AIGamesBotIOHandler - Created on 15-9-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class AIGamesBotIOHandler implements BotIO, Runnable {

    private final int MAX_ERRORS = 2;
    private final String NULL_MOVE1 = "no_moves";
    private final String NULL_MOVE2 = "pass";
    private final long MAX_TIMEBANK = 10000;
    private final long TIME_PER_MOVE = 500;

    private Process process;
    private OutputStreamWriter inputStream;
    private InputStreamGobbler outputGobbler;
    private InputStreamGobbler errorGobbler;
    private StringBuilder dump;
    private int errorCounter;
    private boolean finished;
    private String mongoIdString;
    private long timebank;

    public String response;

    public AIGamesBotIOHandler(Process process, String mongoIdString) {
        this.inputStream = new OutputStreamWriter(process.getOutputStream());
        this.outputGobbler = new InputStreamGobbler(process.getInputStream(), this, "output");
        this.errorGobbler = new InputStreamGobbler(process.getErrorStream(), this, "error");
        this.process = process;
        this.mongoIdString = mongoIdString;
        this.dump = new StringBuilder();
        this.errorCounter = 0;
        this.finished = false;
        this.timebank = MAX_TIMEBANK;

        run();
    }

    @Override
    public void sendMessage(String message) {
        if (this.finished) return;

        try {
            this.inputStream.write(message + "\n");
            this.inputStream.flush();
        } catch(IOException e) {
            System.err.println("Writing to bot failed");
        }
        addToDump(message);
    }

    @Override
    public String sendRequest(String request) {
        if (this.finished) return "";

        long startTime = System.currentTimeMillis();

        sendMessage(String.format("%s %s", request, this.timebank));
        String response = getResponse(this.timebank);

        // Update timebank
        long timeElapsed = System.currentTimeMillis() - startTime;
        updateTimeBank(timeElapsed);

        return response;
    }

    @Override
    public void sendWarning(String warning) {
        if (this.finished) return;

        addToDump(warning);
    }

    /**
     * Wait's until the this.response has a value and then returns that value
     * @param timeOut Time before timeout
     * @return Bot's response, returns and empty string when there is no response
     */
    public String getResponse(long timeOut) {
        long timeStart = System.currentTimeMillis();
        String enginesays = "Output from your bot: ";
        String response;

        if (this.errorCounter > this.MAX_ERRORS) {
            addToDump(String.format("Maximum number (%d) of time-outs reached: " +
                    "skipping all moves.", this.MAX_ERRORS));
            return "";
        }

        while (this.response == null) {
            long timeNow = System.currentTimeMillis();
            long timeElapsed = timeNow - timeStart;

            if (timeElapsed >= timeOut) {
                addToDump(String.format("Response timed out (%dms), let your bot return '%s' " +
                        "instead of nothing or make it faster.", timeOut, this.NULL_MOVE1));
                this.errorCounter++;

                if (this.errorCounter > this.MAX_ERRORS) {
                    finish();
                }

                addToDump(String.format("%snull", enginesays));
                return "";
            }

            try { Thread.sleep(2); } catch (InterruptedException ignored) {}
        }

        if (this.response.equalsIgnoreCase(this.NULL_MOVE1) ||
                this.response.equalsIgnoreCase(this.NULL_MOVE2)) {
            addToDump(String.format("%s\"%s\"", enginesays, this.response));
            this.response = null;
            return "";
        }

        response = this.response;
        this.response = null;

        addToDump(String.format("%s\"%s\"", enginesays, response));
        return response;
    }

    /**
     * Ends the bot process and it's communication
     */
    public void finish() {
        if (this.finished) return;

        // stop the bot's IO
        try { this.inputStream.close(); } catch (IOException ignored) {}
        this.outputGobbler.finish();
        this.errorGobbler.finish();

        // end the bot process
        this.process.destroy();
        try { this.process.waitFor(); } catch (InterruptedException ignored) {}

        this.finished = true;
    }

    /**
     * @return String representation of the bot ID as used in the database
     */
    public String getMongoIdString() {
        return this.mongoIdString;
    }

    /**
     * Adds a string to the bot dump
     * @param dumpy String to add to the dump
     */
    public void addToDump(String dumpy) {
        dump.append(dumpy + "\n");
    }

    /**
     * Add a warning to the bot's dump that the engine outputs
     * @param warning The warning message
     */
    public void outputEngineWarning(String warning) {
        dump.append(String.format("Engine warning: \"%s\"\n", warning));
    }

    /**
     * @return The complete stdOut from the bot process
     */
    public String getStdout() {
        return this.outputGobbler.getData();
    }

    /**
     * @return The complete stdErr from the bot process
     */
    public String getStderr() {
        return this.errorGobbler.getData();
    }

    /**
     * @return The dump of all the IO
     */
    public String getDump() {
        return dump.toString();
    }

    /**
     * Start the communication with the bot
     */
    @Override
    public void run() {
        this.outputGobbler.start();
        this.errorGobbler.start();
    }

    private void updateTimeBank(long timeElapsed) {
        this.timebank = Math.max(this.timebank - timeElapsed, 0);
        this.timebank = Math.min(this.timebank + TIME_PER_MOVE, MAX_TIMEBANK);
    }

    public long getMaxTimebank() {
        return MAX_TIMEBANK;
    }

    public long getTimePerMove() {
        return TIME_PER_MOVE;
    }
}
