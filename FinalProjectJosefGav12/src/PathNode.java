/**
 * Pathnode class used for shortest path algorithm
 * Josef Gavronskiy
 * ICS4U 2024/2025
 * */

public class PathNode {
    public Tile tile; // tile of this node
    public int distance; // distance from start node
    public PathNode previous; // previous node

    public PathNode(Tile tile, int distance, PathNode previous) {
        this.tile = tile;
        this.distance = distance;
        this.previous = previous;
    }
}