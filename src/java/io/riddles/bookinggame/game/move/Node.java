package io.riddles.bookinggame.game.move;
public class Node {

    /** costs to move sideways from one square to another. */
    protected static final int BASICMOVEMENTCOST = 10;
    /** costs to move diagonally from one square to another. */
    protected static final int DIAGONALMOVEMENTCOST = 14;

    private int xPosition;
    private int yPosition;
    private boolean walkable;

    // for pathfinding:

    /** the previous AbstractNode of this one on the currently calculated path. */
    private Node previous;

    /** weather or not the move from previous to this AbstractNode is diagonally. */
    private boolean diagonally;

    /** optional extra penalty. */
    private int movementPanelty;

    //private int fCosts; // g + h costs

    /** calculated costs from start AbstractNode to this AbstractNode. */
    private int gCosts;

    /** estimated costs to get from this AbstractNode to end AbstractNode. */
    private int hCosts;

    /**
     * constructs a walkable AbstractNode with given coordinates.
     *
     * @param xPosition
     * @param yPosition
     */
    public Node(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.walkable = true;
        this.movementPanelty = 0;
    }

    /**
     * returns weather or not the move from the <code>previousAbstractNode</code> was
     * diagonally. If it is not diagonal, it is sideways.
     *
     * @return
     */
    public boolean isDiagonaly() {
        return diagonally;
    }

    /**
     * sets weather or not the move from the <code>previousAbstractNode</code> was
     * diagonally. If it is not diagonal, it is sideways.
     *
     * @param isDiagonaly
     */
    public void setIsDiagonaly(boolean isDiagonaly) {
        this.diagonally = isDiagonaly;
    }

    /**
     * sets x and y coordinates.
     *
     * @param x
     * @param y
     */
    public void setCoordinates(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * @return the xPosition
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * @return the yPosition
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * @return the walkable
     */
    public boolean isWalkable() {
        return walkable;
    }

    /**
     * @param walkable the walkable to set
     */
    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    /**
     * returns the node set as previous node on the current path.
     *
     * @return the previous
     */
    public Node getPrevious() {
        return previous;
    }

    /**
     * @param previous the previous to set
     */
    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    /**
     * sets a general penalty for the movement on this node.
     *
     * @param movementPanelty the movementPanelty to set
     */
    public void setMovementPanelty(int movementPanelty) {
        this.movementPanelty = movementPanelty;
    }

    /**
     * returns <code>gCosts</code> + <code>hCosts</code>.
     * <p>
     *
     *
     * @return the fCosts
     */
    public int getfCosts() {
        return gCosts + hCosts;
    }

    /**
     * returns the calculated costs from start AbstractNode to this AbstractNode.
     *
     * @return the gCosts
     */
    public int getgCosts() {
        return gCosts;
    }

    /**
     * sets gCosts to <code>gCosts</code> plus <code>movementPanelty</code>
     * for this AbstractNode.
     *
     * @param gCosts the gCosts to set
     */
    private void setgCosts(int gCosts) {
        this.gCosts = gCosts + movementPanelty;
    }

    /**
     * sets gCosts to <code>gCosts</code> plus <code>movementPanelty</code>
     * for this AbstractNode given the previous AbstractNode as well as the basic cost
     * from it to this AbstractNode.
     *
     * @param previousAbstractNode
     * @param basicCost
     */
    public void setgCosts(Node previousAbstractNode, int basicCost) {
        setgCosts(previousAbstractNode.getgCosts() + basicCost);
    }

    /**
     * sets gCosts to <code>gCosts</code> plus <code>movementPanelty</code>
     * for this AbstractNode given the previous AbstractNode.
     * <p>
     * It will assume <code>BASICMOVEMENTCOST</code> as the cost from
     * <code>previousAbstractNode</code> to itself if the movement is not diagonally,
     * otherwise it will assume <code>DIAGONALMOVEMENTCOST</code>.
     * Weather or not it is diagonally is set in the Map class method which
     * finds the adjacent AbstractNodes.
     *
     * @param previousAbstractNode
     */
    public void setgCosts(Node previousAbstractNode) {
        if (diagonally) {
            setgCosts(previousAbstractNode, DIAGONALMOVEMENTCOST);
        } else {
            setgCosts(previousAbstractNode, BASICMOVEMENTCOST);
        }
    }

    /**
     * calculates - but does not set - g costs.
     * <p>
     * It will assume <code>BASICMOVEMENTCOST</code> as the cost from
     * <code>previousAbstractNode</code> to itself if the movement is not diagonally,
     * otherwise it will assume <code>DIAGONALMOVEMENTCOST</code>.
     * Weather or not it is diagonally is set in the Map class method which
     * finds the adjacent AbstractNodes.
     *
     * @param previousAbstractNode
     * @return gCosts
     */
    public int calculategCosts(Node previousAbstractNode) {
        if (diagonally) {
            return (previousAbstractNode.getgCosts()
                    + DIAGONALMOVEMENTCOST + movementPanelty);
        } else {
            return (previousAbstractNode.getgCosts()
                    + BASICMOVEMENTCOST + movementPanelty);
        }
    }

    /**
     * calculates - but does not set - g costs, adding a movementPanelty.
     *
     * @param previousAbstractNode
     * @param movementCost costs from previous AbstractNode to this AbstractNode.
     * @return gCosts
     */
    public int calculategCosts(Node previousAbstractNode, int movementCost) {
        return (previousAbstractNode.getgCosts() + movementCost + movementPanelty);
    }

    /**
     * returns estimated costs to get from this AbstractNode to end AbstractNode.
     *
     * @return the hCosts
     */
    public int gethCosts() {
        return hCosts;
    }

    /**
     * sets hCosts.
     *
     * @param hCosts the hCosts to set
     */
    protected void sethCosts(int hCosts) {
        this.hCosts = hCosts;
    }


    /*
     * @return the movementPanelty
     */
    private int getMovementPanelty() {
        return movementPanelty;
    }

    /**
     * returns a String containing the coordinates, as well as h, f and g
     * costs.
     *
     * @return
     */
    @Override
    public String toString() {
        return "(" + getXPosition() + ", " + getYPosition() + "): h: "
                + gethCosts() + " g: " + getgCosts() + " f: " + getfCosts();
    }

    /**
     * returns weather the coordinates of AbstractNodes are equal.
     *
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (this.xPosition != other.xPosition) {
            return false;
        }
        if (this.yPosition != other.yPosition) {
            return false;
        }
        return true;
    }

    /**
     * returns hash code calculated with coordinates.
     *
     * @return
     */
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.xPosition;
        hash = 17 * hash + this.yPosition;
        return hash;
    }
    public void sethCosts(Node endNode) {
        this.sethCosts((absolute(this.getXPosition() - endNode.getXPosition())
                + absolute(this.getYPosition() - endNode.getYPosition()))
                * BASICMOVEMENTCOST);
    }

    private int absolute(int a) {
        return a > 0 ? a : -a;
    }
}