package io.riddles.bookinggame.game.move;
/*
    Copyright (C) 2012 http://software-talk.org/ (developer@software-talk.org)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * // TODO
 * possible optimizations:
 * - calculate f as soon as g or h are set, so it will not have to be
 *      calculated each time it is retrieved
 * - store nodes in openList sorted by their f value.
 */


import io.riddles.bookinggame.game.data.BookingGameBoard;
import io.riddles.bookinggame.game.data.Coordinate;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a simple map.
 * <p>
 * It's width as well as height can be set up on construction.
 * The map can represent nodes that are walkable or not, it can be printed
 * to sto, and it can calculate the shortest path between two nodes avoiding
 * walkable nodes.
 * <p>
 * <p>
 * Usage of this package:
 * Create a node class which extends AbstractNode and implements the sethCosts
 * method.
 * Create a NodeFactory that implements the NodeFactory interface.
 * Create Map instance with those created classes.
 * <p>
 *
 * @version 1.0
 * @param <T>
 */
public class Map<T extends Node> {

    /** weather or not it is possible to walk diagonally on the map in general. */
    protected static boolean CANMOVEDIAGONALY = false;

    /** holds nodes. first dim represents x-, second y-axis. */
    private T[][] nodes;

    /** width + 1 is size of first dimension of nodes. */
    protected int width;
    /** height + 1 is size of second dimension of nodes. */
    protected int height;

    /** a Factory to create instances of specified nodes. */

    /**
     * constructs a squared map with given width and height.
     * <p>
     * The nodes will be instanciated througth the given nodeFactory.
     *
     * @param width
     * @param height
     */
    public Map(int width, int height) {
        // TODO check parameters. width and height should be > 0.
        nodes = (T[][]) new Node[width][height];
        this.width = width - 1;
        this.height = height - 1;
        initEmptyNodes();
    }

    /**
     * initializes all nodes. Their coordinates will be set correctly.
     */
    private void initEmptyNodes() {
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                nodes[i][j] = (T) new Node(i, j);
            }
        }
    }

    /**
     * initializes all nodes. Their coordinates will be set correctly.
     */
    public void initFromBoard(BookingGameBoard b, Coordinate block) {
        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                nodes[i][j] = (T) new Node(i, j);
                nodes[i][j].setWalkable(b.isEmpty(new Coordinate(i, j)));
                if (block != null) {
                    if (i == block.getX() && j == block.getY()) { nodes[i][j].setWalkable(false); System.out.println("setblock " + i + j); }
                }
            }
        }
    }

    /**
     * sets nodes walkable field at given coordinates to given value.
     * <p>
     * x/y must be bigger or equal to 0 and smaller or equal to width/height.
     *
     * @param x
     * @param y
     * @param bool
     */
    public void setWalkable(int x, int y, boolean bool) {
        // TODO check parameter.
        nodes[x][y].setWalkable(bool);
    }

    /**
     * returns node at given coordinates.
     * <p>
     * x/y must be bigger or equal to 0 and smaller or equal to width/height.
     *
     * @param x
     * @param y
     * @return node
     */
    public final T getNode(int x, int y) {
        // TODO check parameter.
        return nodes[x][y];
    }

    /**
     * prints map to sto. Feel free to override this method.
     * <p>
     * a player will be represented as "o", an unwakable terrain as "#".
     * Movement penalty will not be displayed.
     */
    public void drawMap() {
        for (int i = 0; i <= width; i++) {
            print(" _"); // boarder of map
        }
        print("\n");

        for (int j = 0; j <= height; j++) {
            print("|"); // boarder of map
            for (int i = 0; i <= width; i++) {
                if (nodes[i][j].isWalkable()) {
                    print("  ");
                } else {
                    print(" #"); // draw unwalkable
                }
            }
            print("|\n"); // boarder of map
        }

        for (int i = 0; i <= width; i++) {
            print(" _"); // boarder of map
        }
    }

    /**
     * prints something to sto.
     */
    private void print(String s) {
        System.out.print(s);
    }


    /* Variables and methodes for path finding */


    // variables needed for path finding

    /** list containing nodes not visited but adjacent to visited nodes. */
    private List<T> openList;
    /** list containing nodes already visited/taken care of. */
    private List<T> closedList;
    /** done finding path? */
    private boolean done = false;

    /**
     * finds an allowed path from start to goal coordinates on this map.
     * <p>
     * This method uses the A* algorithm. The hCosts value is calculated in
     * the given Node implementation.
     * <p>
     * This method will return a LinkedList containing the start node at the
     * beginning followed by the calculated shortest allowed path ending
     * with the end node.
     * <p>
     * If no allowed path exists, an empty list will be returned.
     * <p>
     * <p>
     * x/y must be bigger or equal to 0 and smaller or equal to width/height.
     *
     * @param Coordinate oldC
     * @param Coordinate newC
     * @return
     */
    public final List<T> findPath(Coordinate oldC, Coordinate newC) {
        int oldX = oldC.getX();
        int oldY = oldC.getY();
        int newX = newC.getX();
        int newY = newC.getY();
        // TODO check input
        openList = new LinkedList<T>();
        closedList = new LinkedList<T>();
        openList.add(nodes[oldX][oldY]); // add starting node to open list

        done = false;
        T current;
        while (!done) {
            current = lowestFInOpen(); // get node with lowest fCosts from openList
            closedList.add(current); // add current node to closed list
            openList.remove(current); // delete current node from open list

            if ((current.getxPosition() == newX)
                    && (current.getyPosition() == newY)) { // found goal
                return calcPath(nodes[oldX][oldY], current);
            }

            // for all adjacent nodes:
            List<T> adjacentNodes = getAdjacent(current);
            for (int i = 0; i < adjacentNodes.size(); i++) {
                T currentAdj = adjacentNodes.get(i);
                if (!openList.contains(currentAdj)) { // node is not in openList
                    currentAdj.setPrevious(current); // set current node as previous for this node
                    currentAdj.sethCosts(nodes[newX][newY]); // set h costs of this node (estimated costs to goal)
                    currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    openList.add(currentAdj); // add node to openList
                } else { // node is in openList
                    if (currentAdj.getgCosts() > currentAdj.calculategCosts(current)) { // costs from current node are cheaper than previous costs
                        currentAdj.setPrevious(current); // set current node as previous for this node
                        currentAdj.setgCosts(current); // set g costs of this node (costs from start to this node)
                    }
                }
            }

            if (openList.isEmpty()) { // no path exists
                return new LinkedList<T>(); // return empty list
            }
        }
        return null; // unreachable
    }

    /**
     * calculates the found path between two points according to
     * their given <code>previousNode</code> field.
     *
     * @param start
     * @param goal
     * @return
     */
    private List<T> calcPath(T start, T goal) {
        // TODO if invalid nodes are given (eg cannot find from
        // goal to start, this method will result in an infinite loop!)
        LinkedList<T> path = new LinkedList<T>();

        T curr = goal;
        boolean done = false;
        while (!done) {
            path.addFirst(curr);
            curr = (T) curr.getPrevious();

            if (curr.equals(start)) {
                done = true;
            }
        }
        return path;
    }

    /**
     * returns the node with the lowest fCosts.
     *
     * @return
     */
    private T lowestFInOpen() {
        // TODO currently, this is done by going through the whole openList!
        T cheapest = openList.get(0);
        for (int i = 0; i < openList.size(); i++) {
            if (openList.get(i).getfCosts() < cheapest.getfCosts()) {
                cheapest = openList.get(i);
            }
        }
        return cheapest;
    }

    /**
     * returns a LinkedList with nodes adjacent to the given node.
     * if those exist, are walkable and are not already in the closedList!
     */
    private List<T> getAdjacent(T node) {
        // TODO make loop
        int x = node.getxPosition();
        int y = node.getyPosition();
        List<T> adj = new LinkedList<T>();

        T temp;
        if (x > 0) {
            temp = this.getNode((x - 1), y);
            if (temp.isWalkable() && !closedList.contains(temp)) {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }

        if (x < width) {
            temp = this.getNode((x + 1), y);
            if (temp.isWalkable() && !closedList.contains(temp)) {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }

        if (y > 0) {
            temp = this.getNode(x, (y - 1));
            if (temp.isWalkable() && !closedList.contains(temp)) {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }

        if (y < height) {
            temp = this.getNode(x, (y + 1));
            if (temp.isWalkable() && !closedList.contains(temp)) {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }


        // add nodes that are diagonaly adjacent too:
        if (CANMOVEDIAGONALY) {
            if (x < width && y < height) {
                temp = this.getNode((x + 1), (y + 1));
                if (temp.isWalkable() && !closedList.contains(temp)) {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }

            if (x > 0 && y > 0) {
                temp = this.getNode((x - 1), (y - 1));
                if (temp.isWalkable() && !closedList.contains(temp)) {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }

            if (x > 0 && y < height) {
                temp = this.getNode((x - 1), (y + 1));
                if (temp.isWalkable() && !closedList.contains(temp)) {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }

            if (x < width && y > 0) {
                temp = this.getNode((x + 1), (y - 1));
                if (temp.isWalkable() && !closedList.contains(temp)) {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
        }
        return adj;
    }

}