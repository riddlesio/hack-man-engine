package io.riddles.bookinggame.game.board;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * io.riddles.bookinggame.game.board.ShortestPath - Created on 31-8-16
 *
 * [description]
 *
 * @author Jim van Eeden - jim@riddles.io
 */
public class ShortestPathHandler {

    private Board board;
    private Node[][] graph;

    public ShortestPathHandler(Board board) {
        int boardHeight = board.getHeight();
        int boardWidth = board.getWidth();

        this.board = board;
        this.graph = new Node[boardWidth][boardHeight];

        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                this.graph[x][y] = new Node(x, y, board, this.graph);
            }
        }
    }

    /**
     * A* algorithm
     * @param start Start point
     * @param goal End point
     * @return List of points from start to goal, shortest path
     */
    public ArrayList<Point> getShortestPath(Point start, Point goal, ArrayList<Point> blockedPoints) {

        // initialize algorithm
        Node startNode = this.graph[start.x][start.y];
        Node goalNode = this.graph[goal.x][goal.y];

        initializeGraph(goalNode);

        for (Point point : blockedPoints) { // block some points on the field
            Node blockedNode = this.graph[point.x][point.y];
            blockedNode.setTraversible(false);
        }

        ArrayList<Node> closedSet = new ArrayList<>();
        ArrayList<Node> openSet = new ArrayList<>();

        startNode.setDistanceScore(0);
        startNode.updateTotalScore();
        openSet.add(startNode);

        // perform algorithm
        while (openSet.size() > 0) {

            // get the node with the lowest score
            Collections.sort(openSet);
            Node current = openSet.get(0);

            // found goal node
            if (current.getLocation().equals(goal)) {
                return reconstructShortestPath(current, startNode);
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Node neighbor : current.getNeighbors()) {
                if (!neighbor.isTraversible() || closedSet.contains(neighbor)) // already evaluated
                    continue;

                int distanceFromHere = current.getDistanceScore() + 1;

                if (openSet.contains(neighbor) && distanceFromHere >= neighbor.getDistanceScore()) // not a better path
                    continue;

                neighbor.setPredecessor(current);
                neighbor.setDistanceScore(distanceFromHere);
                neighbor.updateTotalScore();

                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
            }
        }

        // path can't be found
        return null;
    }

    private void initializeGraph(Node goalNode) {
        for (int y = 0; y < this.board.getHeight(); y++) {
            for (int x = 0; x < this.board.getWidth(); x++) {
                this.graph[x][y].initialize(goalNode);
            }
        }
    }

    /**
     * Returns the shortest path as a list of points (start point not included)
     * @param current Node the algorithm finished on
     * @param start Start node
     * @return Shortest path excluding start point
     */
    private ArrayList<Point> reconstructShortestPath(Node current, Node start) {
        ArrayList<Point> shortestPath = new ArrayList<>();
        shortestPath.add(current.getLocation());

        Node predecessor = current.getPredecessor();
        while (!predecessor.getLocation().equals(start.getLocation())) {
            shortestPath.add(predecessor.getLocation());
            predecessor = predecessor.getPredecessor();
        }

        Collections.reverse(shortestPath);
        return shortestPath;
    }
}
