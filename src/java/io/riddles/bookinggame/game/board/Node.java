package io.riddles.bookinggame.game.board;

import java.awt.*;
import java.util.ArrayList;

/**
 * io.riddles.bookinggame.game.board.Node - Created on 31-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
class Node implements Comparable<Node> {

    private Point location;
    private boolean traversible;
    private ArrayList<Node> neighbors;

    private int distanceScore;
    private double distanceHeuristicScore;
    private double totalScore;
    private Node predecessor;

    Node(int x, int y, Board board, Node[][] graph) {
        this.location = new Point(x, y);
        this.traversible = board.isCoordinateValid(this.location);
        this.neighbors = new ArrayList<>();

        if (x > 0) {
            addNeighbor(graph[x - 1][y]);
        }
        if (y > 0) {
            addNeighbor(graph[x][y - 1]);
        }
    }

    private void addNeighbor(Node neighbor) {
        if (neighbor == null) {
            throw new RuntimeException("Can't add neighor because it doesn't exist yet");
        }

        this.neighbors.add(neighbor);
        neighbor.getNeighbors().add(this);
    }

    void initialize(Node goal) {
        this.distanceScore = Integer.MAX_VALUE;
        this.totalScore = Double.MAX_VALUE;
        this.predecessor = null;

        // Distance heuristic score is euclidean distance to goal
        this.distanceHeuristicScore = this.location.distance(goal.getLocation());
    }

    Point getLocation() {
        return this.location;
    }

    void setTraversible(boolean traversible) {
        this.traversible = traversible;
    }

    boolean isTraversible() {
        return this.traversible;
    }

    ArrayList<Node> getNeighbors() {
        return this.neighbors;
    }

    void setDistanceScore(int score) {
        this.distanceScore = score;
    }

    int getDistanceScore() {
        return this.distanceScore;
    }

    void updateTotalScore() {
        this.totalScore = this.distanceScore + this.distanceHeuristicScore;
    }

    private double getTotalScore() {
        return this.totalScore;
    }

    void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    Node getPredecessor() {
        return this.predecessor;
    }

    @Override
    public int compareTo(Node node) {
        if (this.totalScore < node.getTotalScore()) return -1;
        if (this.totalScore > node.getTotalScore()) return 1;
        return 0;
    }
}
